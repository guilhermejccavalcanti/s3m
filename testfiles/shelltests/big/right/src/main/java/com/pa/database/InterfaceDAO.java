package com.pa.database;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;

public interface InterfaceDAO<T, PK extends Serializable> {

	/**
	 * Save object in the data base
	 * 
	 * @param object to be saved
	 * @return object saved.
	 * @throws HibernateException
	 */
	T save(final T entity);

	/**
	 * Update object in the data base
	 * 
	 * @param object to be updated
	 * @throws HibernateException
	 */
	 void update(final T object);

	/**
	 * Refresh object of data base
	 * 
	 * @param object to be refreshed
	 * @throws HibernateException
	 */
	 void refresh(final T object) throws HibernateException;

	/**
	 * Delete object of data base
	 * 
	 * @param object to be deleted
	 * @throws HibernateException
	 */
	 void delete(final T object) throws HibernateException;

	/**
	 * Get object of data base
	 * 
	 * @param primary key of object
	 * @return object
	 * @throws HibernateException
	 */
	 T get(final PK primaryKey) throws HibernateException;

	/**
	 * List all objects of data base
	 * 
	 * @return {@link List} the objects
	 * @throws HibernateException
	 */
	 List<T> listAll() throws HibernateException;

	/**
	 * List all objects of data base, through the example
	 * 
	 * @param example of object to be listed
	 * @return {@link List} the objects
	 * @throws HibernateException
	 */
	 List<T> listAll(Object example) throws HibernateException;

	/**
	 * List all objects of data base, through the criterion
	 * 
	 * @param {@link Criteria} used to list the objects
	 * @return {@link List} the objects
	 * @throws HibernateException
	 */
	 List<T> listAll(Criteria criteria) throws HibernateException;
	 
	 void merge(T x) throws HibernateException;
}
