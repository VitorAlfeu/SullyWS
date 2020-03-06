package com.ws.sully.api.services.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ws.sully.api.entities.Usuario;
import com.ws.sully.api.repositories.UsuarioRepository;
import com.ws.sully.api.services.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);
	
	@Autowired
	UsuarioRepository usuarioRepository; 
	
	public Usuario persistir (Usuario usuario) {
		log.info("Cadastrando/Atualizando Usuário...");
		return this.usuarioRepository.save(usuario);
	}
	
	public List<Usuario> findInteligentByLogin (String nome){
		log.info("Buscando Usuário por nome e login...");
		return this.usuarioRepository.findInteligentByLogin(nome);
	}
	
	public List<Usuario> findAll(){
		log.info("Buscando todos os Usuários...");
		return this.usuarioRepository.findAll();
	}
	
	public List<Usuario> consultarTodosComLancamentos(){
		log.info("Buscando todos os Usuário que possuem Lançamentos vinculados...");
		return this.usuarioRepository.consultarTodosComLancamentos();
	}
	
	public List<Usuario> consultarTodosSemLancamentos(){
		log.info("Buscando todos os Usuário que não possuem Lançamentos vinculados...");
		return this.usuarioRepository.consultarTodosSemLancamentos();
	}
	
	public Optional<Usuario> findById(long id) {
		log.info("Buscando Usuário por Id...");
		return this.usuarioRepository.findById(id);
	}
	
	public void deleteById(long id) {
		log.info("Excluindo Usuário por Id...");
		this.deleteById(id);
	}
	
	public void deleteAll() {
		log.info("Excluindo todos os Usuários...");
		this.deleteAll();
	}
	
	public Usuario findByLogin(String login) {
		log.info("Buscando Usuário por Login...");
		return this.usuarioRepository.findByLogin(login);
	}
	
	public Usuario findByEmail(String email) {
		log.info("Buscando Usuário por email...");
		return this.usuarioRepository.findByEmail(email);
	}
	
	public Usuario findByLoginAndEmail(String login, String email) {
		log.info("Buscando Usuário por Login e Email...");
		return this.usuarioRepository.findByLoginAndEmail(login, email);
	}
}
