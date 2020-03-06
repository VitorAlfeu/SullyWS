package com.ws.sully.api.dtos;

import java.util.Date;

import javax.validation.constraints.NotEmpty;

public class LancamentoDto {

	private long id;
	private Date dataHora;
	private String status;
	private double valor;
	private long usuario;
	private String loginUsuario;
	private String nomeUsuario;
	private long usuarioDevedor;
	private String loginDevedor;
	private String nomeDevedor;
	private String titulo;
	private String descricao;
	private Date dataHoraAtualizacao;
	private String comentarioAtualizacao;
	private String dataHoraAReceber;
	private Date dataHoraAtualizacaoStatus;

	public LancamentoDto() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@NotEmpty(message = "O Campo 'valor' não pode ser vazio.")
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}

	public long getUsuario() {
		return usuario;
	}
	public void setUsuario(long usuario) {
		this.usuario = usuario;
	}
	
	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public long getUsuarioDevedor() {
		return usuarioDevedor;
	}

	public void setUsuarioDevedor(long usuarioDevedor) {
		this.usuarioDevedor = usuarioDevedor;
	}

	public String getLoginDevedor() {
		return loginDevedor;
	}

	public void setLoginDevedor(String loginDevedor) {
		this.loginDevedor = loginDevedor;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	@NotEmpty(message = "O campo 'titulo' não pode ser vazio.")
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@NotEmpty(message = "O campo 'descricao' não pode ser vazio.")
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataHoraAtualizacao() {
		return dataHoraAtualizacao;
	}

	public void setDataHoraAtualizacao(Date dataHoraAtualizacao) {
		this.dataHoraAtualizacao = dataHoraAtualizacao;
	}

	public String getComentarioAtualizacao() {
		return comentarioAtualizacao;
	}

	public void setComentarioAtualizacao(String comentarioAtualizacao) {
		this.comentarioAtualizacao = comentarioAtualizacao;
	}

	public String getDataHoraAReceber() {
		return dataHoraAReceber;
	}

	public void setDataHoraAReceber(String dataHoraAReceber) {
		this.dataHoraAReceber = dataHoraAReceber;
	}

	public Date getDataHoraAtualizacaoStatus() {
		return dataHoraAtualizacaoStatus;
	}

	public void setDataHoraAtualizacaoStatus(Date dataHoraAtualizacaoStatus) {
		this.dataHoraAtualizacaoStatus = dataHoraAtualizacaoStatus;
	}
}
