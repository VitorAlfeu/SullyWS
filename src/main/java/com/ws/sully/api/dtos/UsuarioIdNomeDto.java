package com.ws.sully.api.dtos;

public class UsuarioIdNomeDto {
	private long id;
	private String login;
	
	public UsuarioIdNomeDto() {	
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String toString() {
		return String.format("UsuarioIdNomeDto [id=%s, login=%s]", id, login);
	}
}
