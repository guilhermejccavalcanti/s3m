package com.pa.database;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import com.pa.database.util.HibernateUtil;

/**
 * 
 * @param <T>
 *            entity representing the table accessed by this generic DAO (Data
 *            Access Object)
 * @param <PK>
 *            type of table's primary key
 */
public abstract class GenericDAO<T, PK extends Serializable> implements InterfaceDAO<T, PK> {

	private Class<T> objectClass;

	protected abstract Class<T> getEntityKlass();      
	
	protected SessionFactory getSessionFactory(){
		return HibernateUtil.getSessionFactory();
	}

	public GenericDAO(Class<T> objectClass) {
		this.objectClass = objectClass;
	}

	public Class<T> getObjectClass() {
		return this.objectClass;
	}

	public T save(T x) throws HibernateException {        
		Session sessao = getSession();
		
		Transaction trans = sessao.beginTransaction();
		sessao.save(x);
		sessao.flush();
		trans.commit();
		
		return x;
	}

	public void update(T x) throws HibernateException {
		Session sessao = getSession();
		
		Transaction trans = sessao.beginTransaction();
		
		sessao.update(x);
		sessao.flush();
		
		trans.commit();
	}
	
	public void merge(T x) throws HibernateException {
		Session sessao = getSession();
		
		Transaction trans = sessao.beginTransaction();
		
		sessao.merge(x);
		sessao.flush();
		
		trans.commit();
	}

	public void refresh(T x) throws HibernateException {
		Session sessao = getSession();
		sessao.beginTransaction();
		sessao.refresh(x);
		sessao.flush();
	}

	public void delete(T x) throws HibernateException {
		Session sessao = getSession();
		
		Transaction trans = sessao.beginTransaction();
		
		sessao.delete(x);
		sessao.flush();
		
		trans.commit();
	}


	@SuppressWarnings("unchecked")
	public T get(PK key) throws HibernateException {

		Session sessao = getSession();
		sessao.beginTransaction();
		T elemento = (T) sessao.get(getEntityKlass(), (Serializable) key);

		return elemento;
	}

	@SuppressWarnings("unchecked")
	public List<T> listAll(){
		return this.createCriteria(this.getEntityKlass()).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> listAll(Object example) throws HibernateException {
		Criteria c = this.createCriteria(example.getClass());
		c.add(Example.create(example));
		return c.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> listAllByQuery(String query) throws HibernateException {
		getSession().beginTransaction();
		return getSession().createQuery(query).list();

	}

	@SuppressWarnings("unchecked")
	public List<T> listAll(Criteria c) throws HibernateException {
		getSession().beginTransaction();			
		return c.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> listAll(int first, int max) throws HibernateException {

		Criteria c = getSession().createCriteria(this.getEntityKlass());

		if (first != 0) {
			c.setFirstResult(first);
		}
		if (max != 0) {
			c.setMaxResults(max);
		}
		return c.list();
	}

	/**
	 * Create object {@link Criteria} of a particular class
	 * 
	 * @return {@link Criteria}
	 */
	public Criteria createCriteria(Class classe) {
		getSession().beginTransaction();
		Criteria c = getSession().createCriteria(classe);

		return c;
	}

	/**
	 * Create object {@link Criteria} of a particular class
	 * 
	 * @return {@link Criteria}
	 */
	public Criteria createCriteria(Class classe, String alias) {

		Criteria c = getSession().createCriteria(classe, alias);

		return c;
	}

	/**
	 * Get session opened of the Hibernate
	 * 
	 * @return {@link Session}
	 */
	public Session getSession() {
		Session oSession = getSessionFactory().getCurrentSession();
		return oSession;
	}


}
