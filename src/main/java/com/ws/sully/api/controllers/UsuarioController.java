package com.ws.sully.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ws.sully.api.dtos.UsuarioDto;
import com.ws.sully.api.dtos.UsuarioIdNomeDto;
import com.ws.sully.api.entities.Usuario;
import com.ws.sully.api.enums.StatusUsuarioEnum;
import com.ws.sully.api.response.Response;
import com.ws.sully.api.services.UsuarioService;
import com.ws.sully.api.utils.FunctionUtils;
import com.ws.sully.api.utils.PasswordUtils;
import com.ws.sully.api.utils.StringsLocaisUtils;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat dateFormatDataHora = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private JavaMailSender mailSender;
	
	@GetMapping(value="/testeEmail")
	public String testarEmail() {
		if (enviarEmailHtml("Código de Confirmação", "772311", "teste@hotmail.com", String.format(StringsLocaisUtils.MENSAGEM_CONFIRMACAO_EMAIL, "Teste"))){
			return "Sucesso";
		} else {
			return "Falha";	
		}		 
	}
	
	@PostMapping
	public ResponseEntity<Response<UsuarioDto>> cadastrarUsuario(@Valid @RequestBody UsuarioDto usuarioDto, BindingResult result) throws ParseException {
		log.info("Adicionando Usuário: {}", usuarioDto.toString());
		Response<UsuarioDto> response = new Response<UsuarioDto>();
		
		if (!FunctionUtils.isNotNullOrEmpty(usuarioDto.getStatus())) {
			usuarioDto.setStatus("AGUARDANDO_CONFIRMACAO_EMAIL");
		}
		
		validarCaracterEspecial(usuarioDto.getLogin(), result);
		validarLoginExistente(usuarioDto.getLogin(), result);
		validarEmailExistente(usuarioDto.getEmail(), result);
		if (!validarData(usuarioDto.getDataNasc(), "yyyy-MM-dd")) {
			result.addError(new ObjectError("usuario", "O campo 'dataNasc' está incorreta!"));
		}
		
		String codConfirmEmail = gerarCodigoAleatorio(6);
		usuarioDto.setCodConfirmEmail(codConfirmEmail);
		Usuario usuario = converterDtoEmUsuario(usuarioDto, result);
		
		if (result.hasErrors()) {
			log.error("Erro ao Cadastrar Usuário: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		
		usuario = usuarioService.persistir(usuario);
		if (!enviarEmailHtml("Código de Confirmação", codConfirmEmail, usuarioDto.getEmail(), String.format(StringsLocaisUtils.MENSAGEM_CONFIRMACAO_EMAIL, usuarioDto.getNome()))) {
			result.addError(new ObjectError("usuario", "Erro ao enviar E-mail de confirmação!"));
		}
		
		if (result.hasErrors()) {
			log.error("Erro ao cadastrar Usuário: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
					
		List<UsuarioDto> listUsuarioDto = new LinkedList<>();
		listUsuarioDto.add(converterUsuarioEmDto(usuario));
		response.setData(listUsuarioDto);
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping(value="/login")
	public ResponseEntity<Response<UsuarioDto>> logarUsuario(@RequestBody String json, BindingResult result) throws JSONException{
		Response<UsuarioDto> response = new Response<UsuarioDto>();
		JSONObject jsonObj = new JSONObject(json);		
		Usuario usuario = buscarUsuarioLogin(jsonObj, result);
		
		if (result.hasErrors()) {
			log.error("Erro ao fazer login de Usuário: {}", result.getAllErrors());
			result.getAllErrors().forEach(erro -> response.getErrors().add(erro.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		List<UsuarioDto> listUsuarioDto = new LinkedList<>();
		listUsuarioDto.add(converterUsuarioEmDto(usuario));
		response.setData(listUsuarioDto);
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping(value="/confirmaremail")
	public String confirmarUsuario(@RequestBody String json) throws JSONException{
		log.info("Confirmando E-mail...");
		String msgRetorno;
		JSONObject jsonObj = new JSONObject(json);
		if (jsonObj.isNull("id") || jsonObj.isNull("codConfirmEmail")) {
			msgRetorno = "As propriedades da Request não estão corretas";
			log.info(msgRetorno);
			return msgRetorno;
		}
		
		Optional<Usuario> usuario = usuarioService.findById(jsonObj.getLong("id"));
		if (usuario.get().getStatus().equals(StatusUsuarioEnum.EMAIL_CONFIRMADO)) {
			msgRetorno = "O Email para este Usuário já foi confirmado";
			log.info(msgRetorno);
			return msgRetorno;
		}
		
		if (codConfirmEsgotado(usuario.get().getDtHrEnvioConfirmEmail())) {
			msgRetorno = "O tempo para confirmação com esse código foi esgotado!";
			log.info(msgRetorno);
			return msgRetorno;
		}
		
		boolean codigosBatem = PasswordUtils.descriptografarString(jsonObj.getString("codConfirmEmail"), usuario.get().getCodConfirmEmail());
		if (codigosBatem) {
			usuario.get().setStatus(StatusUsuarioEnum.EMAIL_CONFIRMADO);
			usuarioService.persistir(usuario.get());
			msgRetorno = "Email confirmado com Sucesso!";
			log.info(msgRetorno);
			return msgRetorno;			
		}
		
		msgRetorno = "Email não confirmado!";
		log.info(msgRetorno);
		return msgRetorno;
	}
	
	@PostMapping(value="/reenviarconfirmacaoemail")
	public ResponseEntity<String> reenviarConfirmacaoEmail(@RequestBody String json) throws JSONException {
		JSONObject jsonObj = new JSONObject(json);
		if (jsonObj.isNull("login") || jsonObj.isNull("email")) {
			return ResponseEntity.badRequest().body("As propriedades da Request não estão corretas");
		}
		
		Usuario usuario = usuarioService.findByLoginAndEmail(jsonObj.getString("login"), jsonObj.getString("email"));
		if (!FunctionUtils.isNotNullOrEmpty(usuario)) {
			return ResponseEntity.badRequest().body("Usuário não encontrado!");
		}
		
		if (usuario.getStatus().equals(StatusUsuarioEnum.EMAIL_CONFIRMADO)) {
			return ResponseEntity.badRequest().body("Usuário com E-mail já confirmado!");
		}
		
		String novoCodConfirm = gerarCodigoAleatorio(6);
		usuario.setCodConfirmEmail(PasswordUtils.criptografarString(novoCodConfirm));
		usuario.setDtHrEnvioConfirmEmail(new Date());
		usuarioService.persistir(usuario);
		
		if (enviarEmailHtml("Código de confirmação SullyApp", novoCodConfirm, usuario.getEmail(), String.format(StringsLocaisUtils.MENSAGEM_CONFIRMACAO_EMAIL, usuario.getNome()))) {
			return ResponseEntity.ok("Código de Confirmação reenviado por E-mail com Sucesso!");
		} else {
			return ResponseEntity.badRequest().body("Erro ao reenviar E-mail com o Código de Confirmação!");
		}				
	}
	
	@PutMapping(value="/recuperarsenha")
	public String recuperarSenha(@RequestBody String json, BindingResult result) throws JSONException {
		JSONObject jsonObj = new JSONObject(json);
		if (jsonObj.isNull("login") || jsonObj.isNull("email")) {
			return "As propriedades da Request não estão corretas";
		}
		Usuario usuario = usuarioService.findByLoginAndEmail(jsonObj.getString("login"), jsonObj.getString("email"));
		
		if (FunctionUtils.isNotNullOrEmpty(usuario)) {
			String novaSenha = gerarStringAleatoria(8);
			usuario.setSenha(PasswordUtils.criptografarString(novaSenha));
			usuarioService.persistir(usuario);
			
			if (enviarEmailHtml("Nova Senha para o SullyApp", novaSenha, usuario.getEmail(), String.format(StringsLocaisUtils.MENSAGEM_NOVA_SENHA, usuario.getNome()))) {
				return "Senha de recuperação enviada por E-mail com Sucesso!";
			} else {
				return "Erro ao enviar E-mail para Recuperação de Senha!";
			}
		}
		return "Não foi possível enviar E-mail para Recuperação de Senha!";
	}
	
	@PostMapping(value="/cadastroemmassa")
	public String cadastroEmMassa(@RequestBody String json) throws JSONException {
		JSONObject jsonObj = new JSONObject(json);
		JSONArray jsonArray = jsonObj.getJSONArray("nomes");
		for(int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
			String nome = jsonObject.getString("nome");
			int qtdARodar = Math.round(jsonObj.getInt("qtd") / jsonArray.length());
			
			for (int j = 0; j < qtdARodar; j++) {
				Usuario usuario = new Usuario();
				usuario.setLogin(nome + "1");
				usuario.setSenha("123");
				usuario.setNome(nome + " da Silva ");
				usuario.setEmail(nome + "@hotmail.com");
				usuario.setDataNasc(new Date(System.currentTimeMillis()));
				usuario.setApelido(nome + "zin");
				usuario.setCodConfirmEmail("123456");
				usuario.setStatus(StatusUsuarioEnum.valueOf("AGUARDANDO_CONFIRMACAO_EMAIL"));
				usuarioService.persistir(usuario);
			}
		}
		return "Cadastros efetuados com sucesso.";
	}
	
	@GetMapping(value="/todos")
	public List<Usuario> buscarUsuariosTodos(){
		return usuarioService.findAll();
	}
	
	@GetMapping(value="/buscarpornome/{nome}")
	public ResponseEntity<Response<UsuarioIdNomeDto>> buscarUsuario(@PathVariable("nome") String nome) {
		Response<UsuarioIdNomeDto> responseList = new Response<>();
		List<UsuarioIdNomeDto> usuarioIdNomeDto = new LinkedList<>();
		List<Usuario> usuario = usuarioService.findInteligentByLogin(nome + "%");
		usuario.forEach(usuarioEncontrado -> usuarioIdNomeDto.add(converterUsuarioemIdNomeDto(usuarioEncontrado)));
		responseList.setData(usuarioIdNomeDto);
		return ResponseEntity.ok().body(responseList);
	}
	
	@GetMapping(value="/buscarporemail/{email}")
	public ResponseEntity<Response<UsuarioDto>> buscarEmail(@PathVariable("email") String email) {
		Response<UsuarioDto> response = new Response<>();
		Usuario usuario = usuarioService.findByEmail(email);
		if (!FunctionUtils.isNotNullOrEmpty(usuario)) {
			log.error("Nenhum Usuário encontrado com o E-mail informado!");
			response.getErrors().add("Nenhum Usuário encontrado com o E-mail informado!");
			return ResponseEntity.badRequest().body(response);
		}
		UsuarioDto usuarioDto = new UsuarioDto();
		usuarioDto.setId(usuario.getId());
		usuarioDto.setLogin(usuario.getLogin());
		usuarioDto.setEmail(usuario.getEmail());
		
		List<UsuarioDto> lstUsuarioDto = new LinkedList<>();
		lstUsuarioDto.add(usuarioDto);
		response.setData(lstUsuarioDto);
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping(value="/todos/{comsemlancamentos}")
	public ResponseEntity<List<UsuarioDto>> buscarUsuarioComLancamentos(@PathVariable("comsemlancamentos") String comsemlancamentos){		
		List<Usuario> usuarios = new LinkedList<>();		
		if (comsemlancamentos.equalsIgnoreCase("comlancamentos")) {
			 usuarios = usuarioService.consultarTodosComLancamentos();
		} else if (comsemlancamentos.equalsIgnoreCase("semlancamentos")){
			usuarios = usuarioService.consultarTodosSemLancamentos();
		} else if (comsemlancamentos.equalsIgnoreCase("comesemlancamentos")){
			usuarios = usuarioService.findAll();			
		} else {
			return ResponseEntity.badRequest().build(); 
		}
		List<UsuarioDto> usuariosDto = new LinkedList<>();
		for(Usuario usuario : usuarios) {
			usuariosDto.add(converterUsuarioEmDto(usuario)); 			
		}
		return ResponseEntity.ok().body(usuariosDto);
	}
	
	@PutMapping
	public ResponseEntity<Response<UsuarioDto>> atualizarUsuario(@RequestBody UsuarioDto usuarioDto, BindingResult result) throws ParseException {
		Usuario usuario = converterDtoEmUsuario(usuarioDto, result);
		Response<UsuarioDto> response = new Response<UsuarioDto>();		
		
		if (result.hasErrors()) {
			log.error("Erro ao Atualizar Usuário: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		return usuarioService.findById(usuarioDto.getId())
		.map(record -> {
			if (FunctionUtils.isNotNullOrEmpty(usuario.getLogin())) {
				if (!record.getLogin().equalsIgnoreCase(usuario.getLogin())) {
					validarLoginExistente(usuario.getLogin(), result);
				}
				if (result.hasErrors()) {
					log.error("Erro ao Atualizar Usuário: {}", result.getAllErrors());
					result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
					return ResponseEntity.badRequest().body(response);
				}
				record.setLogin(usuario.getLogin());
			}
			if (FunctionUtils.isNotNullOrEmpty(usuario.getSenha())) {
				record.setSenha(usuario.getSenha());
			}
			if (FunctionUtils.isNotNullOrEmpty(usuario.getApelido())) {
				record.setApelido(usuario.getApelido());
			}
			if (FunctionUtils.isNotNullOrEmpty(usuario.getDataNasc())) {
				record.setDataNasc(usuario.getDataNasc());
			}
			if (FunctionUtils.isNotNullOrEmpty(usuario.getEmail())) {
				if (!record.getEmail().equalsIgnoreCase(usuario.getEmail())) {
					validarEmailExistente(usuario.getEmail(), result);
				}
				if (result.hasErrors()) {
					log.error("Erro ao Atualizar Usuário: {}", result.getAllErrors());
					result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
					return ResponseEntity.badRequest().body(response);
				}
				record.setEmail(usuario.getEmail());
			}
			record.setLogado(usuario.getLogado());
			if (FunctionUtils.isNotNullOrEmpty(usuario.getNome())) {			
				record.setNome(usuario.getNome());
			}
			if (FunctionUtils.isNotNullOrEmpty(usuario.getStatus())){
				record.setStatus(usuario.getStatus());
			}
			if (FunctionUtils.isNotNullOrEmpty(usuario.getDtHrEnvioConfirmEmail())) {
				record.setDtHrEnvioConfirmEmail(usuario.getDtHrEnvioConfirmEmail());
			}
			Usuario updated = usuarioService.persistir(record);
			List<UsuarioDto> listUsuarioDto = new LinkedList<>();
			listUsuarioDto.add(converterUsuarioEmDto(updated));
			response.setData(listUsuarioDto);
			return ResponseEntity.ok().body(response);
		}).orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<String> excluirUsuario(@PathVariable("id") long id) {
		try {
			if(!usuarioService.findById(id).isPresent()) {
				return ResponseEntity.badRequest().body("Nenhum Usuário encontrado com o Id: " + String.valueOf(id));
			}
			usuarioService.deleteById(id);
			return ResponseEntity.ok().body("Usuário excluído com sucesso!");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Erro ao excluir Usuário  " + e);
		}
	}
	
	@DeleteMapping(value="/todos")
	public ResponseEntity<?> excluirUsuarioTodos(){
		if (usuarioService.findAll().isEmpty()) {
			return ResponseEntity.badRequest().body("Nenhum Usuário encontrado para a exclusão!");
		}
		usuarioService.deleteAll();		
		return ResponseEntity.ok().body("Todos os usuários foram excluídos com Sucesso!");
	}
	
	private Usuario converterDtoEmUsuario(UsuarioDto usuarioDto, BindingResult result) throws ParseException {
		Usuario usuario = new Usuario();
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getApelido())) {
			usuario.setApelido(usuarioDto.getApelido());
		}
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getDataNasc())) {		
			usuario.setDataNasc(this.dateFormat.parse(usuarioDto.getDataNasc()));
		}
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getEmail())) {
			usuario.setEmail(usuarioDto.getEmail());
		}
		usuario.setLogado(usuarioDto.getLogado());
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getLogin())) {
			usuario.setLogin(usuarioDto.getLogin());
		}
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getNome())) {		
			usuario.setNome(usuarioDto.getNome());
		}
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getSenha())) {		
			usuario.setSenha(PasswordUtils.criptografarString(usuarioDto.getSenha()));
		}
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getStatus())) {
			usuarioDto.setStatus(usuarioDto.getStatus().toUpperCase());
			if (EnumUtils.isValidEnum(StatusUsuarioEnum.class, usuarioDto.getStatus())) {
				usuario.setStatus(StatusUsuarioEnum.valueOf(usuarioDto.getStatus()));
			} else {
				result.addError(new ObjectError("status", "Status inválido!"));
			}
		}
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getDtHrEnvioConfirmEmail())) {
			usuario.setDtHrEnvioConfirmEmail(this.dateFormatDataHora.parse(usuarioDto.getDtHrEnvioConfirmEmail()));
		}
		if (FunctionUtils.isNotNullOrEmpty(usuarioDto.getCodConfirmEmail())) {
			usuario.setCodConfirmEmail(PasswordUtils.criptografarString(usuarioDto.getCodConfirmEmail()));
		}
		return usuario;
	}
	
	private UsuarioDto converterUsuarioEmDto(Usuario usuario) {
		UsuarioDto usuarioDto = new UsuarioDto();
		usuarioDto.setId(usuario.getId());
		usuarioDto.setApelido(usuario.getApelido());
		usuarioDto.setDataNasc(dateFormat.format(usuario.getDataNasc()));
		usuarioDto.setEmail(usuario.getEmail());
		usuarioDto.setLogado(usuario.getLogado());
		usuarioDto.setLogin(usuario.getLogin());
		usuarioDto.setNome(usuario.getNome());
		usuarioDto.setStatus(usuario.getStatus().toString());
		usuarioDto.setDataHora(dateFormatDataHora.format(usuario.getDataHora()));
		usuarioDto.setDtHrEnvioConfirmEmail(dateFormatDataHora.format(usuario.getDtHrEnvioConfirmEmail()));
		//usuarioDto.setSenha(usuario.getSenha()); comentado, pois a senha é confidencial e não deve ser exibida
		return usuarioDto;
	}
	
	private UsuarioIdNomeDto converterUsuarioemIdNomeDto(Usuario usuario) {
		UsuarioIdNomeDto usuarioIdNomeDtoEncontrado = new UsuarioIdNomeDto();
		usuarioIdNomeDtoEncontrado.setId(usuario.getId());
		usuarioIdNomeDtoEncontrado.setLogin(usuario.getLogin());
		
		return usuarioIdNomeDtoEncontrado;
	}
	
	public boolean validarData(String strDate, String simpleDateFormat) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
		 try {
			 format.setLenient(false);
			 format.parse(strDate);
			 return true;
		 } catch (ParseException e) {
			 return false;
		 }
	}
	
	private void validarLoginESenhaEmBranco(JSONObject jsonObj, BindingResult result) throws JSONException {
		if (!FunctionUtils.isNotNullOrEmpty(jsonObj.getString("login"))) { 
			result.addError(new ObjectError("usuario", "O Campo 'login' não pode ficar em branco!"));
		}
		if (!FunctionUtils.isNotNullOrEmpty(jsonObj.getString("senha"))) {
			result.addError(new ObjectError("usuario", "O Campo 'senha' não pode ficar em branco!"));
		}
	}
	
	private void validarLoginExistente(String login, BindingResult result) {
		if (usuarioService.findByLogin(login) != null) {
			result.addError(new ObjectError("usuario", String.format("O Usuário informado já está cadastrado!", login)));
		}
	}
	
	private void validarEmailExistente(String email, BindingResult result) {
		if (usuarioService.findByEmail(email) != null) {
			result.addError(new ObjectError("usuario", String.format("O Email informado já está cadastrado!", email)));
		}
	}
	
	private Usuario buscarUsuarioLogin(JSONObject jsonObj, BindingResult result) throws JSONException {
		validarLoginESenhaEmBranco(jsonObj, result);
		if (result.hasErrors()) {
			return null;
		}
		Usuario usuario = usuarioService.findByLogin(jsonObj.getString("login"));
		if (usuario == null) {
			result.addError(new ObjectError("usuario", "Usuário e/ou senha incorretos."));
			return null;
		}
		if (usuario.getStatus().equals(StatusUsuarioEnum.AGUARDANDO_CONFIRMACAO_EMAIL)) {
			result.addError(new ObjectError("usuario", "É necessário confirmar o E-mail deste Usuário para prosseguir com o Login!"));
			return null;
		}
		boolean senhaBate = PasswordUtils.descriptografarString(jsonObj.getString("senha"), usuario.getSenha());
		if (!senhaBate) {
			result.addError(new ObjectError("usuario", "Usuário e/ou senha incorretos."));
			return null;
		}		
		return usuario;
	}
	
	public void validarCaracterEspecial(String palavra, BindingResult result) {
		String caracteresValidos = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890";
		for (int i = 0; i < palavra.length(); i++) {
			if (!caracteresValidos.contains(String.valueOf(palavra.charAt(i)))) {
				result.addError((new ObjectError("usuario", "O campo 'login' possui Caracteres Especiais.")));
				break;
			}
		}
	}
	
	private String gerarCodigoAleatorio(int tamanho) {
		Random random = new Random();
		String codigo = "";
		for (int i = 0; i< tamanho; i++) {
			codigo += String.valueOf((random.nextInt(9))); 
		}
		
		return codigo;
	}
	
	private String gerarStringAleatoria(int tamanho) {
		final char[] ALL_CHARS = new char[94];
		final Random RANDOM = new Random();

		for (int i = 33, j = 0; i < 127; i++, j++) {
			ALL_CHARS[j] = (char) i;
		}

		final char[] result = new char[tamanho];
		for (int i = 0; i < tamanho; i++) {
			result[i] = ALL_CHARS[RANDOM.nextInt(ALL_CHARS.length)];
		}
		
		return String.valueOf(result);
	}
	
	
//	private boolean enviarEmail(String titulo, String texto, String email){
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setSubject(titulo);
//		message.setText(texto);
//		message.setTo(email);
//
//		try {
//			log.info("Enviando email...");
//			mailSender.send(message);
//			log.info("Email enviado!");
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.info("Erro ao enviar E-mail!");
//			return false;			
//		}
//	}
	
	private boolean enviarEmailHtml(String titulo, String texto, String emailDestino, String corpoTexto) {
		try {
            MimeMessage mail = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mail);
            helper.setTo(emailDestino);
            helper.setSubject(titulo);
            helper.setText(getHtml(titulo, texto, corpoTexto), true);
            
            log.info("Enviando email...");
            mailSender.send(mail);
            log.info("Email enviado!");
			return true;
        } catch (Exception e) {
        	log.info("Erro ao enviar E-mail!");
			System.out.println(e);
			return false;
        }
	}
	
	private boolean codConfirmEsgotado(Date dataHr) { 
		LocalDateTime ldtDtHrEnvioConfirmEmail = dataHr.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		long tempoDeEnvio = ldtDtHrEnvioConfirmEmail.until(LocalDateTime.now(), ChronoUnit.MINUTES);

		return (tempoDeEnvio > 10);
	}
	
	private String getHtml(String titulo, String texto, String corpoTexto) {
		StringBuilder html = new StringBuilder();
		
		html.append("<center>");
		html.append("        <div style= \"width: 400px; height: 500px; text-align:center; font-style:italic; border: 2px solid #000000\">");
		html.append("        <div style= \"border-bottom: 2px solid #000000; padding: 10px 0px; position:relative; background-color: rgb(51, 163, 90); text-align: center; width:400px; height:auto; display:block; font-style:italic; font-family: Georgia; color:rgb(255, 255, 255); font-size:52px;\">SullyApp</div>");
		html.append("        <div style= \"width: 100%; margin: 0; height: 80%; display: table; position: relative;\">");
		html.append("        	 <div style= \"width: 100%; padding: 0px 10px; text-align: center; display: table-cell; vertical-align: middle;\">");
		html.append("        	 		<p style=\"text-align:center; font-family:Georgia; width: 100%; font-size: 20px;\">" + corpoTexto + "</p>");
		html.append("            		<h2 style =\"float:left; width:100%; display:block; width: 100%; text-align: center; font-style:italic; font-size: 25px; color: rgb(51, 163, 90);\">" + titulo + "</h2>");
		html.append("            		<center><input type=\"text\" value=\" "+ texto +" \" readonly style = \"background-origin: shadow 5px; width:40%; display:block; font-family:Georgia; font-size: 25px; text-align: center;\"/></center>");
		html.append("        	 </div>");
		html.append("    	</div>");
		html.append("    	</div>");
		html.append("</center>");
		
		return html.toString();
	}
}