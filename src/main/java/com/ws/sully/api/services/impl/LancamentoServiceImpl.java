package com.ws.sully.api.services.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ws.sully.api.entities.Lancamento;
import com.ws.sully.api.repositories.LancamentoRepository;
import com.ws.sully.api.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService{

	private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);
	
	@Autowired
	LancamentoRepository lancamentoRepository;
	
	public Lancamento persistir(Lancamento lancamento) {
		log.info("Cadastrando/Atualizando Lançamento...");
		return this.lancamentoRepository.save(lancamento);
	}

	public List<Lancamento> consultarLancamentosPorPeriodo(String dataInicial, String dataFinal){
		log.info("Consultando Lançamentos por Período...");
		return this.lancamentoRepository.consultarLancamentosPorPeriodo(dataInicial, dataFinal);
	}
	
	public List<Lancamento> consultarLancamentosPorUsuarioEPeriodo(long usuarioId, String dataInicial, String dataFinal){
		log.info("Consultando Lançamentos por Usuário e por Período...");
		return this.lancamentoRepository.consultarLancamentosPorUsuarioEPeriodo(usuarioId, dataInicial, dataFinal); 
	}

	public Optional<Lancamento> findById(long id){
		log.info("Consultando Lançamentos por Id...");
		return this.lancamentoRepository.findById(id);
	}
	
	public void deleteById(long id) {
		log.info("Excluindo Lançamento por Id...");
		this.lancamentoRepository.deleteById(id);
	}
	
}