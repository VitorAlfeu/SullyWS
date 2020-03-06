package com.ws.sully.api.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ws.sully.api.enums.StatusLancamentoEnum;

@Entity
@Table(name="lancamentos")
public class Lancamento implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private long id; 
	private Usuario usuario;	
	private Usuario usuarioDevedor;
	private double valor;
	private Date dataHora;
	private StatusLancamentoEnum status;
	private String titulo;
	private String descricao;
	private Date dataHoraAtualizacao;
	private String comentarioAtualizacao;
	private Date dataHoraAReceber;
	private Date dataHoraAtualizacaoStatus;
	
	public Lancamento() {		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}	
	public void setId(long id) {
		this.id = id;
	}
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	public Usuario getUsuarioDevedor() {
		return usuarioDevedor;
	}

	public void setUsuarioDevedor(Usuario usuarioDevedor) {
		this.usuarioDevedor = usuarioDevedor;
	}
	
	@Column(name = "valor", nullable = false)
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	
	@Column(name = "data_hora", nullable = false)
	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = true, columnDefinition = "VARCHAR(60) DEFAULT 'AGUARDANDO_PAGAMENTO'")
	public StatusLancamentoEnum getStatus() {
		return status;
	}
	public void setStatus(StatusLancamentoEnum status) {
		this.status = status;
	}
	
	@Column(name = "titulo", nullable = false)
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "descricao", nullable = false)
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "dt_hr_atualizacao", nullable = true)
	public Date getDataHoraAtualizacao() {
		return dataHoraAtualizacao;
	}
	public void setDataHoraAtualizacao(Date dataHoraAtualizacao) {
		this.dataHoraAtualizacao = dataHoraAtualizacao;
	}

	@Column(name = "comentario_atualizacao", nullable = true)
	public String getComentarioAtualizacao() {
		return comentarioAtualizacao;
	}
	public void setComentarioAtualizacao(String comentarioAtualizacao) {
		this.comentarioAtualizacao = comentarioAtualizacao;
	}

	@Column(name = "dt_hr_a_receber", nullable = true)
	public Date getDataHoraAReceber() {
		return dataHoraAReceber;
	}
	public void setDataHoraAReceber(Date dataHoraAReceber) {
		this.dataHoraAReceber = dataHoraAReceber;
	}

	@Column(name = "dt_hr_atualizacao_status", nullable = true)
	public Date getDataHoraAtualizacaoStatus() {
		return dataHoraAtualizacaoStatus;
	}
	public void setDataHoraAtualizacaoStatus(Date dataHoraAtualizacaoStatus) {
		this.dataHoraAtualizacaoStatus = dataHoraAtualizacaoStatus;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@PreUpdate
	public void preUpdate() {
		this.dataHoraAtualizacao = new Date(System.currentTimeMillis());
	}
	
    @PrePersist
    public void prePersist() {
        dataHora = new Date(System.currentTimeMillis());
    }
}
