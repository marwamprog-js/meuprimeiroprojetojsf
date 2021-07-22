package br.com.meuprimeiroprojetojsf.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.meuprimeiroprojetojsf.util.JPAUtil;

public class DaoGeneric<E> {


	/*
	 * SALVAR
	 * */
	public void salvar(E entidade) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		entityManager.persist(entidade);

		transaction.commit();
		entityManager.close();

	}


	
	/*
	 * SALVAR / ATUALIZAR retornando a ENTIDADE
	 * */
	public E merge(E entidade) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		E retorno = entityManager.merge(entidade);

		transaction.commit();
		entityManager.close();

		
		return retorno;
	}
	
	
	/*
	 * DELETE
	 * */
	public void delete(E entidade) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		entityManager.remove(entidade);

		transaction.commit();
		entityManager.close();

	}

	
	/*
	 * DELETE por ID
	 * */
	public void deletePorId(E entidade) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		Object id = JPAUtil.getPrimeiryKey(entidade);
		
		entityManager.createQuery("delete from " + entidade.getClass().getCanonicalName() + " where id = " + id).executeUpdate();

		transaction.commit();
		entityManager.close();

	}
	
	
	
	/*
	 * LISTAR
	 * */
	public List<E> findAll(Class<E> entidade){
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<E> retorno = entityManager.createQuery("from " + entidade.getName() + " order by id").getResultList();
		
		transaction.commit();
		entityManager.close();

		
		return retorno;
	}
	
	
	


}
