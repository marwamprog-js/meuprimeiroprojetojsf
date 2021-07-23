package br.com.meuprimeiroprojetojsf.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.meuprimeiroprojetojsf.model.Estados;
import br.com.meuprimeiroprojetojsf.util.JPAUtil;

@FacesConverter(forClass = Estados.class, value = "converterEstado")
public class EstadoConverter implements Converter, Serializable{

	/*
	 * Retorna Objeto Inteiro
	 * */
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String codigoEstado) {
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Estados estado = (Estados) entityManager.find(Estados.class, Long.parseLong(codigoEstado));
				
		return estado;
	}

	
	
	/*
	 * Retorna apenas o codigo em String
	 * */
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object estado) {
				
		if(estado == null) {
			return null;
		}
		
		if(estado instanceof Estados) {
			return ((Estados) estado).getId().toString();
		} else {
			return estado.toString();
		}
		
		
	}

}
