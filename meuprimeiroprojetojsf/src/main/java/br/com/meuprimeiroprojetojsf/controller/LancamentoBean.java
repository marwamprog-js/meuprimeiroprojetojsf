package br.com.meuprimeiroprojetojsf.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.meuprimeiroprojetojsf.dao.DaoGeneric;
import br.com.meuprimeiroprojetojsf.model.Lancamento;
import br.com.meuprimeiroprojetojsf.model.Pessoa;
import br.com.meuprimeiroprojetojsf.repository.IDaoLancamento;
import br.com.meuprimeiroprojetojsf.repository.IDaoLancamentoImpl;

@ViewScoped
@ManagedBean(name = "lancamentoBean")
public class LancamentoBean {

	private Lancamento lancamento = new Lancamento();
	private DaoGeneric<Lancamento> dao = new DaoGeneric<Lancamento>();
	
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	private IDaoLancamento daoLancamento = new IDaoLancamentoImpl();

	
	
	/*
	 * SALVAR
	 * */
	public String salvar() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoa = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		
		lancamento.setUsuario(pessoa);
		
		lancamento = dao.merge(lancamento);
		
		
		carregarLancamentos();
		
		
		return "";
	}
	
	
	
	/*
	 * CARREGAR LANÇAMENTOS de acordo com usuário logado
	 * */
	@PostConstruct
	public void carregarLancamentos() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoa = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		
		lancamentos = daoLancamento.consultar(pessoa.getId());
		
	}
	
	
	
	
	/*
	 * NOVO
	 * */
	public String novo() {
		
		lancamento = new Lancamento();
		
		return "";
	}
	
	
	/*
	 * REMOVER
	 * */
	public String remover() {
		
		dao.deletePorId(lancamento);
		lancamento = new Lancamento();
		carregarLancamentos();
		
		return "";
	}
	
	
	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public DaoGeneric<Lancamento> getDao() {
		return dao;
	}

	public void setDao(DaoGeneric<Lancamento> dao) {
		this.dao = dao;
	}

	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}
	
	
	
	
}
