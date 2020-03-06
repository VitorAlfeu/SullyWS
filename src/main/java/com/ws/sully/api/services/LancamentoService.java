package com.ws.sully.api.services;

import java.util.List;
import java.util.Optional;

import com.ws.sully.api.entities.Lancamento;

public interface LancamentoService {

	Lancamento persistir(Lancamento lancamento);

	List<Lancamento> consultarLancamentosPorPeriodo(String dataInicial, String dataFinal);

	List<Lancamento> consultarLancamentosPorUsuarioEPeriodo(long usuarioId, String dataInicial, String dataFinal);

	Optional<Lancamento> findById(long id);

	void deleteById(long id);
	
}
