package br.com.meuprimeiroprojetojsf.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.meuprimeiroprojetojsf.dao.DaoGeneric;
import br.com.meuprimeiroprojetojsf.model.Pessoa;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> dao = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	
	/*
	 * SALVAR ou ATUALIZAR
	 * */
	public String salvar() {		
		pessoa = dao.merge(pessoa);
		carregarPessoas();
		return "";
	}
	
	
	
	/*
	 * NOVO cadastro
	 * */
	public String novo() {
		
		pessoa = new Pessoa();
		return "";
	}
	
	
	/*
	 * REMOVER Usuário
	 * */
	public String remove() {
		
		dao.deletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		return "";
	}
	
	/*
	 * LISTAR
	 * */
	@PostConstruct
	public void carregarPessoas() {
		pessoas = dao.findAll(Pessoa.class);
	}
	
	
	
	
	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public DaoGeneric<Pessoa> getDao() {
		return dao;
	}
	public void setDao(DaoGeneric<Pessoa> dao) {
		this.dao = dao;
	}

	
	
}
