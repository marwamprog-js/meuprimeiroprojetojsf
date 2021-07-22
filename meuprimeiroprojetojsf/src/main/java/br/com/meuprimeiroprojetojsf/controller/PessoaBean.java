package br.com.meuprimeiroprojetojsf.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.meuprimeiroprojetojsf.dao.DaoGeneric;
import br.com.meuprimeiroprojetojsf.model.Pessoa;
import br.com.meuprimeiroprojetojsf.repository.IDaoPessoa;
import br.com.meuprimeiroprojetojsf.repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> dao = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	
	
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
	
	
	
	/*
	 * LOGIN
	 * */
	public String logar() {
		
		Pessoa pessoaBanco = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		
		if(pessoaBanco != null) {
			
			//Adicionar usuario na sessão
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			
			/*
			 * Caso der errooooo de SESSAO
			HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
			HttpSession session = req.getSession();
			session.setAttribute("usuarioLogado", pessoaBanco);
			*/
			
			externalContext.getSessionMap().put("usuarioLogado", pessoaBanco);
			
			
			return "primeira-pagina.jsf";
		}
		
		return "index.jsf";
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
