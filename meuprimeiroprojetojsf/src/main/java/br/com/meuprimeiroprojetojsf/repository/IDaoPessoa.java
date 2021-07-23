package br.com.meuprimeiroprojetojsf.repository;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.meuprimeiroprojetojsf.model.Pessoa;

public interface IDaoPessoa {

	Pessoa consultarUsuario(String login, String senha);
	List<SelectItem> listaEstados();
	
}
