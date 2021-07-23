package br.com.meuprimeiroprojetojsf.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import br.com.meuprimeiroprojetojsf.dao.DaoGeneric;
import br.com.meuprimeiroprojetojsf.model.Cidades;
import br.com.meuprimeiroprojetojsf.model.Estados;
import br.com.meuprimeiroprojetojsf.model.Pessoa;
import br.com.meuprimeiroprojetojsf.repository.IDaoPessoa;
import br.com.meuprimeiroprojetojsf.repository.IDaoPessoaImpl;
import br.com.meuprimeiroprojetojsf.util.JPAUtil;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> dao = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();

	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	private List<SelectItem> estados;
	private List<SelectItem> cidades;

	/*
	 * SALVAR ou ATUALIZAR
	 * */
	public String salvar() {		
		pessoa = dao.merge(pessoa);
		carregarPessoas();
		mostrarMsg("Cadastrado com Sucesso!!");
		return "";
	}


	/*
	 * Consumindo RESTFull
	 * Consulta CEP
	 * */
	public void pesquisaCep(AjaxBehaviorEvent event) {

		try {

			URL url = new URL("https://viacep.com.br/ws/"+ pessoa.getCep() +"/json/");
			URLConnection connection = url.openConnection();			
			InputStream is = connection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";
			StringBuilder jsonCep = new StringBuilder();

			while((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}

			Pessoa gsonAux = new Gson().fromJson(jsonCep.toString(), Pessoa.class);

			pessoa.setCep(gsonAux.getCep());
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setComplemento(gsonAux.getComplemento());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());
			pessoa.setIbge(gsonAux.getIbge());




		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("Erro ao consultar o CEP.");
		}

	}


	/*
	 * Mostrar mensagem quando SALVAR / EXCLUIR
	 * */
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
	}



	/*
	 * NOVO cadastro
	 * */
	public String novo() {

		pessoa = new Pessoa();
		return "";
	}


	/*
	 * LIMPAR
	 * */
	public String limpar() {

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
		mostrarMsg("Removido com sucesso");
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


	/*
	 * DESLOGAR
	 * */
	public String deslogar() {

		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");

		HttpServletRequest httpServletRequest = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();
		httpServletRequest.getSession().invalidate();


		return "index.jsf";
	}




	/*
	 * Permissão de acesso
	 * */
	public boolean permitirAcesso(String acesso) {

		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoa = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");

		return pessoa.getPerfilUser().equals(acesso);
	}



	/*
	 * Carrega Cidades
	 * */
	public void carregaCidades(AjaxBehaviorEvent event) {

		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();

		if(estado != null) {
			pessoa.setEstados(estado);

			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id = " + estado.getId())
					.getResultList();

			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();

			for (Cidades cidade : cidades) {
				selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
			}


			setCidades(selectItemsCidade);

		}



	}



	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}
	public List<SelectItem> getCidades() {
		return cidades;
	}
	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
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
