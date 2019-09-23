package com.pa.database.impl;

import org.hibernate.HibernateException;

import com.pa.database.GenericDAO;
import com.pa.entity.Book;

public class BookDAO extends GenericDAO<Book, Long> {

	public BookDAO(Class<Book> objectClass) {
		super(objectClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Class<Book> getEntityKlass() {
		return Book.class;
	}
	
	@Override
	public Book save(Book x) throws HibernateException {
		return super.save(x);
	}

}
