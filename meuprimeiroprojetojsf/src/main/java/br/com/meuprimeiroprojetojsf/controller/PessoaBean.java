package br.com.meuprimeiroprojetojsf.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

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
	
	private Part arquivoFoto;
	
	
	/*
	 * Registra Log
	 * */
	public void registraLog() {
		System.out.println("método registra log");
	}
	
	
	/*
	 * Mundança de valor
	 * */
	public void mudancaDeValor(ValueChangeEvent event) {
		System.out.println("Valor antigo: " + event.getOldValue());
		System.out.println("Valor novo: " + event.getNewValue());
	}

	/*
	 * SALVAR ou ATUALIZAR
	 * */
	public String salvar() throws IOException {
		
		//Processar Imagem
		byte[] imagemByte = getByte(arquivoFoto.getInputStream());
		pessoa.setFotoIconBase64Original(imagemByte); //Salva Imagem Original
		
		//Transformar em um bufferImage
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));
		
		//Pegar o tipo da imagem
		int type = bufferedImage.getType() == 0 ? bufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
		int largura = 200;
		int altura = 200;
		
		//Criar a miniatura
		BufferedImage resizedImage = new BufferedImage(largura, altura, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(bufferedImage, 0, 0, largura, altura, null);
		g.dispose();
		
		//Escrever novamente a imagem em tamanho menor
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String extensao = arquivoFoto.getContentType().split("\\/")[1]; //Retorno: image/png
		ImageIO.write(resizedImage, extensao, baos);
		
		String miniImagem = "data:"+ arquivoFoto.getContentType() + ";base64," +
				DatatypeConverter.printBase64Binary(baos.toByteArray());
		
		
		
		
		pessoa.setFotoIconBase64(miniImagem);
		pessoa.setExtensao(extensao);
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
	
	
	
	
	/*
	 * EDITAR
	 * */
	public void editar() {
		
		if(pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);
			
			
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



	public void setArquivoFoto(Part arquivoFoto) {
		this.arquivoFoto = arquivoFoto;
	}
	public Part getArquivoFoto() {
		return arquivoFoto;
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

	
	
	/*
	 * Convertendo IMAGEM OU ARQUIVOS em bytes
	 * InputStream em Array de Bytes
	 * */
	private byte[] getByte(InputStream is) throws IOException {
		
		int len;
		int size = 1024;
		byte[] buf = null;
		
		if(is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			
			while((len = is.read(buf, 0, size)) != -1) {
				bos.write(buf, 0, len);
			}
			
			buf = bos.toByteArray();
		}
		
		
		return buf;		
		
	}
	
	
	
	/*
	 * DOWNLOAD da IMG
	 * */
	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String fileDownloadId = params.get("fileDownloadId");
		
		Pessoa pessoa = dao.consultar(Pessoa.class, fileDownloadId);
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();		
		response.addHeader("Content-Disposition", "attachment; filename=download." + pessoa.getExtensao());
		response.setContentType("application/octet-stream");
		response.setContentLength(pessoa.getFotoIconBase64Original().length);
		response.getOutputStream().write(pessoa.getFotoIconBase64Original());
		response.getOutputStream().flush(); //Confirmar resposta de dados.
		FacesContext.getCurrentInstance().responseComplete();
		
	}


}
