package com.ws.sully.api.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ws.sully.api.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	/**
	 * A função abaixo é uma convenção do Spring, onde ele
	 * gera um SELECT a partir do parâmetro passado
	 * @param Login
	 * @return
	 */
	Usuario findByLogin(String login);	

	/**
	 * A função abaixo é uma convenção do Spring, onde ele
	 * DELETA um Usuário a partir do parâmetro passado
	 * @param Login
	 * @return
	 */
	Usuario deleteByLogin(String login);
	
	/**
	 * A função abaixo é uma convenção do Spring, onde ele
	 * gera um SELECT a partir do parâmetro passado
	 * @param Login
	 * @param Senha
	 * @return
	 */
	Usuario findByLoginAndSenha(String login, String senha);

	/**
	 * A função abaixo é uma convenção do Spring, onde ele
	 * gera um SELECT a partir do parâmetro passado
	 * @param Email
	 * @return
	 */
	Usuario findByEmail(String email);
	
	Usuario findByLoginAndEmail(String login, String email);
	
	@Query(value = "SELECT U.ID, U.APELIDO, U.DATA_NASC, U.EMAIL, U.LOGADO\r\n" + 
			", U.LOGIN, U.NOME, U.SENHA, U.STATUS, L.USUARIO_ID, LA.USUARIO_DEVEDOR_ID \r\n" + 
			"FROM USUARIOS U \r\n" + 
			"LEFT JOIN LANCAMENTOS L ON L.USUARIO_ID = U.ID \r\n" + 
			"LEFT JOIN LANCAMENTOS LA ON LA.USUARIO_DEVEDOR_ID = U.ID \r\n" + 
			"WHERE L.USUARIO_ID IS NOT NULL OR LA.USUARIO_DEVEDOR_ID IS NOT NULL", nativeQuery = true)
	List<Usuario> consultarTodosComLancamentos();
	
	@Query(value = "SELECT U.ID, U.APELIDO, U.DATA_NASC, U.EMAIL, U.LOGADO\r\n" + 
			", U.LOGIN, U.NOME, U.SENHA, U.STATUS, L.USUARIO_ID, LA.USUARIO_DEVEDOR_ID \r\n" + 
			"FROM USUARIOS U \r\n" + 
			"LEFT JOIN LANCAMENTOS L ON L.USUARIO_ID = U.ID \r\n" + 
			"LEFT JOIN LANCAMENTOS LA ON LA.USUARIO_DEVEDOR_ID = U.ID \r\n" + 
			"WHERE L.USUARIO_ID IS NULL AND LA.USUARIO_DEVEDOR_ID IS NULL", nativeQuery = true)
	List<Usuario> consultarTodosSemLancamentos();

	@Query(value = "SELECT * FROM usuarios WHERE (login LIKE :nome) OR (nome LIKE :nome)", nativeQuery = true)
	List<Usuario> findInteligentByLogin(@Param("nome") String nome);
	
}
