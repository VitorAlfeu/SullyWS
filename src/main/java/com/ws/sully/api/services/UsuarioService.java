package com.ws.sully.api.services;

import java.util.List;
import java.util.Optional;

import com.ws.sully.api.entities.Usuario;

public interface UsuarioService {
	
	Usuario persistir(Usuario usuario);

	List<Usuario> findInteligentByLogin(String nome);

	List<Usuario> findAll();

	List<Usuario> consultarTodosComLancamentos();

	List<Usuario> consultarTodosSemLancamentos();

	Optional<Usuario> findById(long id);

	void deleteById(long id);

	void deleteAll();
	
	Usuario findByLogin(String login);
	
	Usuario findByEmail(String email);
	
	Usuario findByLoginAndEmail(String login, String email);
}
