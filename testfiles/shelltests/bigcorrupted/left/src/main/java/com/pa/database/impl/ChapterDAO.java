package com.pa.database.impl;

import org.hibernate.HibernateException;

import com.pa.database.GenericDAO;
import com.pa.entity.Chapter;

public class ChapterDAO extends GenericDAO<Chapter, Long> {

	public ChapterDAO(Class<Chapter> objectClass) {
		super(objectClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Class<Chapter> getEntityKlass() {
		return Chapter.class;
	}
	
	@Override
	public Chapter save(Chapter x) throws HibernateException {
		return super.save(x);
	}

}
