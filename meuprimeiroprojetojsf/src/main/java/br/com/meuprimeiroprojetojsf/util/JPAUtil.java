package br.com.meuprimeiroprojetojsf.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

	private static EntityManagerFactory factory = null;

	static {
		if(factory == null) {
			factory = Persistence.createEntityManagerFactory("meuprimeiroprojetojsf");
		}
	}

	
	
	public static EntityManager getEntityManager() {
		return factory.createEntityManager();
	}
	
	
	
	/*
	 * Retorna o id da entidade no banco
	 * */
	public static Object getPrimeiryKey(Object entity) {
		return factory.getPersistenceUnitUtil().getIdentifier(entity);
	}

}
