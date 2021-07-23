package br.com.meuprimeiroprojetojsf.repository;

import java.util.List;

import br.com.meuprimeiroprojetojsf.model.Lancamento;

public interface IDaoLancamento {

	List<Lancamento> consultar(Long codUser);
	
}
