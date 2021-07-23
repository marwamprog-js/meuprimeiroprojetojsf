package br.com.meuprimeiroprojetojsf.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.meuprimeiroprojetojsf.model.Cidades;
import br.com.meuprimeiroprojetojsf.model.Estados;
import br.com.meuprimeiroprojetojsf.util.JPAUtil;

@FacesConverter(forClass = Cidades.class, value = "converterCidade")
public class CidadeConverter implements Converter, Serializable{

	/*
	 * Retorna Objeto Inteiro
	 * */
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String codigoCidade) {
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Cidades cidade = (Cidades) entityManager.find(Cidades.class, Long.parseLong(codigoCidade));
				
		return cidade;
	}

	
	
	/*
	 * Retorna apenas o codigo em String
	 * */
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object cidade) {
				
		if(cidade == null) {
			return null;
		}
		
		if(cidade instanceof Cidades) {
			return ((Cidades) cidade).getId().toString();
		} else {
			return cidade.toString();
		}
	}

}
