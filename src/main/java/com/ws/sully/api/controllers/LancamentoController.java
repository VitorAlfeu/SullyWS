package com.ws.sully.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ws.sully.api.dtos.LancamentoDto;
import com.ws.sully.api.entities.Lancamento;
import com.ws.sully.api.entities.Usuario;
import com.ws.sully.api.enums.StatusLancamentoEnum;
import com.ws.sully.api.response.Response;
import com.ws.sully.api.services.LancamentoService;
import com.ws.sully.api.services.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
	
	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	LancamentoService lancamentoService; 
	
	@Autowired
	UsuarioService usuarioService;
	
	@PostMapping
	public ResponseEntity<Response<LancamentoDto>> cadastrarLancamento(@RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		log.info("Adicionando Lançamento: {}", lancamentoDto.toString());
		if (lancamentoDto.getDataHoraAReceber() != null) {
			lancamentoDto.setDataHoraAReceber(lancamentoDto.getDataHoraAReceber().replace("T"," "));
		}
		System.out.println(lancamentoDto.getDataHoraAReceber());
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		if (lancamentoDto.getStatus() == null) {
			lancamentoDto.setStatus("AGUARDANDO_PAGAMENTO");
		}
		Lancamento lancamento = converterDtoEmLancamento(lancamentoDto, result);
		
		if (result.hasErrors()) {
			log.error("Erro ao Cadastrar Lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		log.info("Adicionando Lançamentos: {}", lancamento.toString());
		lancamento = lancamentoService.persistir(lancamento);
		List<LancamentoDto> listLancamentoDto = new LinkedList<>();
		listLancamentoDto.add(converterLancamentoEmDto(lancamento));
		
		response.setData(listLancamentoDto);
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping(value = "/porperiodo")
	public ResponseEntity<Response<LancamentoDto>> buscarLancamentoPorPeriodo(@RequestBody String json, BindingResult result) throws JSONException{
		JSONObject jsonObj = new JSONObject(json);
		Response<LancamentoDto> response = new Response<>();
		List<Lancamento> lancamentos = new LinkedList<>();
		
		if (jsonObj.isNull("usuarioId")) {
			lancamentos = lancamentoService.consultarLancamentosPorPeriodo(jsonObj.getString("dataInicial"), jsonObj.getString("dataFinal"));
		} else {
			lancamentos = lancamentoService.consultarLancamentosPorUsuarioEPeriodo(jsonObj.getLong("usuarioId")
					, jsonObj.getString("dataInicial"), jsonObj.getString("dataFinal"));
		}
		
		if (lancamentos.isEmpty()) {
			result.addError(new ObjectError("lancamento", "Nenhum lançamento encontrado para o período informado!"));
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		List<LancamentoDto> lancamentosDto = new LinkedList<>();
		lancamentos.forEach(lancamento -> lancamentosDto.add(converterLancamentoEmDto(lancamento)));
		response.setData(lancamentosDto);
		
		return ResponseEntity.ok().body(response);
	}
	
	@PutMapping
	public ResponseEntity<Response<LancamentoDto>> atualizarLancamento(@RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException{
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		Lancamento lancamento = converterDtoEmLancamento(lancamentoDto, result);
		
		if (result.hasErrors()) {
			log.error("Erro ao Atualizar Lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		return lancamentoService.findById(lancamentoDto.getId())
		.map(record -> {			
			if (lancamento.getUsuario().getId() != 0) {
				record.setUsuario(lancamento.getUsuario());
			}
			if (lancamento.getUsuarioDevedor().getId() != 0) {
				record.setUsuarioDevedor(lancamento.getUsuarioDevedor());
			}
			if (lancamento.getValor() != 0.0) {
				log.info(Double.toString(lancamento.getValor()));
				record.setValor(lancamento.getValor());
			}
			if (lancamento.getStatus() != null) {
				record.setStatus(lancamento.getStatus());
			}
			if ((lancamento.getTitulo() != null) && (!lancamento.getTitulo().isEmpty())) {
				record.setTitulo(lancamento.getTitulo());
			}
			if ((lancamento.getDescricao() != null) && (!lancamento.getDescricao().isEmpty())) {
				record.setDescricao(lancamento.getDescricao());
			}
			if ((lancamento.getComentarioAtualizacao() != null) && (!lancamento.getComentarioAtualizacao().isEmpty())) {
				record.setComentarioAtualizacao(lancamento.getComentarioAtualizacao());
			}
			if (lancamento.getDataHoraAReceber() != null) {
				record.setDataHoraAReceber(lancamento.getDataHoraAReceber());
			}
			Lancamento updated = lancamentoService.persistir(record);
			List<LancamentoDto> listLancamentoDto = new LinkedList<>();
			listLancamentoDto.add(converterLancamentoEmDto(updated)); 
			response.setData(listLancamentoDto);
			return ResponseEntity.ok().body(response);
		}).orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<String> excluirUsuario(@PathVariable("id") long id){
		try {
			if (!lancamentoService.findById(id).isPresent()) {
				return ResponseEntity.badRequest().body("Nenhum Lançamento encontrado com o Id: " + String.valueOf(id));
			}			
			lancamentoService.deleteById(id);
			return ResponseEntity.ok().body("Lançamento excluído com sucesso!");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Erro ao excuir Lançamento  " + e);
		}
	}
	
	private Lancamento converterDtoEmLancamento(LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		Lancamento lancamento = new Lancamento();
		
		lancamento.setId(lancamentoDto.getId());
		if ((lancamentoDto.getUsuario() != 0) && (!usuarioService.findById(lancamentoDto.getUsuario()).isPresent())) {
			result.addError(new ObjectError("lancamento", "Nenhum Usuário encontrado com o Id: " + String.valueOf(lancamentoDto.getUsuario())));
		}
		lancamento.setUsuario(new Usuario());		
		lancamento.getUsuario().setId(lancamentoDto.getUsuario());
		if ((lancamentoDto.getUsuarioDevedor() != 0) && (!usuarioService.findById(lancamentoDto.getUsuarioDevedor()).isPresent())) {
			result.addError(new ObjectError("lancamento", "Nenhum Usuário encontrado com o Id: " + String.valueOf(lancamentoDto.getUsuarioDevedor())));
		}
		lancamento.setUsuarioDevedor(new Usuario());
		lancamento.getUsuarioDevedor().setId(lancamentoDto.getUsuarioDevedor());
		lancamento.setValor(lancamentoDto.getValor());
		if (lancamentoDto.getStatus() != null) {
			lancamentoDto.setStatus(lancamentoDto.getStatus().toUpperCase());
			if (EnumUtils.isValidEnum(StatusLancamentoEnum.class, lancamentoDto.getStatus())) {
				lancamento.setStatus(StatusLancamentoEnum.valueOf(lancamentoDto.getStatus()));
			} else {
				result.addError(new ObjectError("status", "Status inválido!"));
			}			
		}		
		lancamento.setTitulo(lancamentoDto.getTitulo());
		log.info(lancamento.getTitulo());
		lancamento.setDescricao(lancamentoDto.getDescricao());
		log.info(lancamento.getDescricao());
		lancamento.setDataHoraAtualizacao(lancamentoDto.getDataHoraAtualizacao());
		lancamento.setComentarioAtualizacao(lancamentoDto.getComentarioAtualizacao());
		if (lancamentoDto.getDataHoraAReceber() != null) {
			validarData(lancamentoDto.getDataHoraAReceber(), result);
			if (!result.hasErrors()) {
				lancamento.setDataHoraAReceber(dateFormat.parse(lancamentoDto.getDataHoraAReceber()));
			}
		}
		lancamento.setDataHoraAtualizacaoStatus(lancamentoDto.getDataHoraAtualizacaoStatus());
		
		return lancamento;
	}
	
	private LancamentoDto converterLancamentoEmDto(Lancamento lancamento) {
		LancamentoDto lancamentoDto = new LancamentoDto();
		
		lancamentoDto.setId(lancamento.getId());
		lancamentoDto.setDataHora(lancamento.getDataHora());
		lancamentoDto.setStatus(lancamento.getStatus().toString());
		lancamentoDto.setValor(lancamento.getValor());
		lancamentoDto.setUsuario(lancamento.getUsuario().getId());
		lancamentoDto.setLoginUsuario(lancamento.getUsuario().getLogin());
		lancamentoDto.setNomeUsuario(lancamento.getUsuario().getNome());
		lancamentoDto.setUsuarioDevedor(lancamento.getUsuarioDevedor().getId());
		lancamentoDto.setLoginDevedor(lancamento.getUsuarioDevedor().getLogin());
		lancamentoDto.setNomeDevedor(lancamento.getUsuarioDevedor().getNome());
		lancamentoDto.setTitulo(lancamento.getTitulo());
		lancamentoDto.setDescricao(lancamento.getDescricao());
		lancamentoDto.setDataHoraAtualizacao(lancamento.getDataHoraAtualizacao());
		lancamentoDto.setComentarioAtualizacao(lancamento.getComentarioAtualizacao());
		SimpleDateFormat formatComT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (lancamentoDto.getDataHoraAReceber() != null) {
			lancamentoDto.setDataHoraAReceber(formatComT.format(lancamento.getDataHoraAReceber()));
		}
		lancamentoDto.setDataHoraAtualizacaoStatus(lancamento.getDataHoraAtualizacaoStatus());
		
		return lancamentoDto;
	}
	
	public void validarData(String strDate, BindingResult result) throws ParseException {
		 try {
			 dateFormat.setLenient(false);
			 dateFormat.parse(strDate);
		 } catch (ParseException e) {
			 result.addError(new ObjectError("usuario", String.format("O campo 'dataHoraAReceber' é uma data inválida!")));
		 }
	}
}