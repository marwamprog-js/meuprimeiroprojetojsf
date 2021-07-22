package br.com.meuprimeiroprojetojsf.repository;

import br.com.meuprimeiroprojetojsf.model.Pessoa;

public interface IDaoPessoa {

	Pessoa consultarUsuario(String login, String senha);
	
}
