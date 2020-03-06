package com.ws.sully.api.entities;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.ws.sully.api.enums.StatusUsuarioEnum;

@Entity
@Table(name="usuarios")
public class Usuario implements Serializable{

	private static final long serialVersionUID = -1835825322768953282L;
	
	private long id;
	private String login;
	private String nome;
	private String apelido;
	private Date dataNasc;
	private String email; 
	private String senha;
	private boolean logado;
	private StatusUsuarioEnum status;
	private Date dataHora;
	private Date dtHrEnvioConfirmEmail;
	private String codConfirmEmail;
	private List<Lancamento> lancamentos;
	private List<Lancamento> lancamentosDevendo;
	
	public Usuario() {
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name = "login", nullable = false)
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "nome", nullable = false)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "apelido", nullable = true)
	public String getApelido() {
		return apelido;
	}
	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	@Column(name = "data_nasc", nullable = false)
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	public Date getDataNasc() {
		return dataNasc;
	}
	public void setDataNasc(Date dataNasc) {
		this.dataNasc = dataNasc;
	}

	@Column(name = "email", nullable = false)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "senha", nullable = false)
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Column(name = "logado", nullable = false, columnDefinition = "BIT default 0")
	public boolean getLogado() {
		return logado;
	}
	public void setLogado(boolean logado) {
		this.logado = logado;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "VARCHAR(60) DEFAULT 'AGUARDANDO_CONFIRMACAO_EMAIL'")
	public StatusUsuarioEnum getStatus() {
		return status;
	}
	
	public void setStatus(StatusUsuarioEnum status) {
		this.status = status;
	}
	
	@Column(name = "data_hora", nullable = false)
	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	@Column(name = "dt_hr_envio_confirm_email", nullable = false)
	public Date getDtHrEnvioConfirmEmail() {
		return dtHrEnvioConfirmEmail;
	}

	public void setDtHrEnvioConfirmEmail(Date dtHrEnvioConfirmEmail) {
		this.dtHrEnvioConfirmEmail = dtHrEnvioConfirmEmail;
	}

	@Column(name = "cod_confirm_email", nullable = false)
	public String getCodConfirmEmail() {
		return codConfirmEmail;
	}

	public void setCodConfirmEmail(String codConfirmEmail) {
		this.codConfirmEmail = codConfirmEmail;
	}

	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}
	
	@OneToMany(mappedBy = "usuarioDevedor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public List<Lancamento> getLancamentosDevendo() {
		return lancamentosDevendo;
	}

	public void setLancamentosDevendo(List<Lancamento> lancamentosDevendo) {
		this.lancamentosDevendo = lancamentosDevendo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@PrePersist
	private void prePersist() throws ParseException {
		this.dtHrEnvioConfirmEmail = new Date();
		this.dataHora = new Date();
	}
	
}