package com.pa.database.impl;

import org.hibernate.HibernateException;

import com.pa.database.GenericDAO;
import com.pa.entity.Orientation;

public class OrientationDAO extends GenericDAO<Orientation, Long> {

	public OrientationDAO(Class<Orientation> objectClass) {
		super(objectClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Class<Orientation> getEntityKlass() {
		return Orientation.class;
	}
	
	@Override
	public Orientation save(Orientation x) throws HibernateException {
		return super.save(x);
	}

}
