package com.ws.sully.api.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

public class UsuarioDto {
	private long id;
	private String login;
	private String nome;
	private String apelido;
	private String dataNasc;
	private String email; 
	private String senha;
	private boolean logado;
	private String status;
	private String dataHora;
	private String dtHrEnvioConfirmEmail;
	private String codConfirmEmail;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@NotEmpty(message = "O campo 'login' não pode ser vazio.")
	@Length(min = 3, max = 20, message = "O campo 'login' deve conter entre 3 e 50 caracteres.")
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	@NotEmpty(message = "O campo 'nome' não pode ser vazio.")
	@Length(min = 3, max = 50, message = "O campo 'nome' deve conter entre 3 e 200 caracteres.")
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Length(min = 3, max = 20, message = "O campo 'apelido' deve conter entre 3 e 200 caracteres.")
	public String getApelido() {
		return apelido;
	}
	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	@DateTimeFormat(pattern = "yyyy/MM/dd")
	public String getDataNasc() {
		return dataNasc;
	}
	public void setDataNasc(String dataNasc) {
		this.dataNasc = dataNasc;
	}

	@NotEmpty(message = "Email não pode ser vazio.")
	@Length(min = 5, max = 200, message = "O campo 'email' deve conter entre 5 e 200 caracteres.")
	@Email(message="Campo 'email' inválido.")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@NotEmpty(message = "O campo 'senha' não pode ser vazio.")
	@Length(min = 3, max = 50, message = "O campo 'senha' deve conter entre 3 e 200 caracteres.")	
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public boolean getLogado() {
		return logado;
	}
	public void setLogado(boolean logado) {
		this.logado = logado;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getDataHora() {
		return dataHora;
	}
	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}
	
	public String getDtHrEnvioConfirmEmail() {
		return dtHrEnvioConfirmEmail;
	}
	public void setDtHrEnvioConfirmEmail(String dtHrEnvioConfirmEmail) {
		this.dtHrEnvioConfirmEmail = dtHrEnvioConfirmEmail;
	}
	
	public String getCodConfirmEmail() {
		return codConfirmEmail;
	}
	public void setCodConfirmEmail(String codConfirmEmail) {
		this.codConfirmEmail = codConfirmEmail;
	}
	
	@Override
	public String toString() {
		return String.format(
				"UsuarioDto [id=%s, login=%s, nome=%s, apelido=%s, dataNasc=%s, email=%s, senha=%s, logado=%s]", id,
				login, nome, apelido, dataNasc, email, senha, logado);
	}
}
