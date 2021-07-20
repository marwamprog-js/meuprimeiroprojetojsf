package br.com.meuprimeiroprojetojsf.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@SessionScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private String nome;
	private String senha;
	private String texto;

	private List<String> nomes = new ArrayList<String>();



	public String addNome() {

		if(nomes.size() > 3) {
			return "pagina-navegada?faces-redirect=true";
		}

		nomes.add(nome);
		return "";
	}



	public String getSenha() {
		return senha;
	}



	public void setSenha(String senha) {
		this.senha = senha;
	}



	public String getTexto() {
		return texto;
	}



	public void setTexto(String texto) {
		this.texto = texto;
	}



	public List<String> getNomes() {
		return nomes;
	}
	public void setNomes(List<String> nomes) {
		this.nomes = nomes;
	}	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}



}
