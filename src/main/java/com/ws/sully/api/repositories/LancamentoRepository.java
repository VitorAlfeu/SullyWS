package com.ws.sully.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ws.sully.api.entities.Lancamento;
import com.ws.sully.api.entities.Usuario;


@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{
	
	/**
	 * A função abaixo é uma convenção do JPA Repository, onde ele
	 * gera um SELECT a partir do parâmetro passado
	 * @param usuario
	 * @return
	 */
	Lancamento findByUsuario(Usuario usuario);

	@Query(value = "SELECT * FROM lancamentos WHERE ((usuario_id = :usuarioId) OR (usuario_devedor_id = :usuarioId)) AND (data_hora BETWEEN :dataInicial AND :dataFinal)", nativeQuery = true)
	List<Lancamento> consultarLancamentosPorUsuarioEPeriodo(@Param("usuarioId") Long idUsuario, @Param("dataInicial") String dataInicial, @Param("dataFinal") String dataFinal);
	
	@Query(value = "SELECT * FROM lancamentos WHERE data_hora BETWEEN :dataInicial AND :dataFinal", nativeQuery = true)
	List<Lancamento> consultarLancamentosPorPeriodo(@Param("dataInicial") String dataInicial, @Param("dataFinal") String dataFinal);
}