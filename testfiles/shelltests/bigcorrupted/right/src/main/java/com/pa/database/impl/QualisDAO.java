package com.pa.database.impl;

import com.pa.database.GenericDAO;
import com.pa.entity.Qualis;

class QualisDAO extends GenericDAO<Qualis, Long> {

	public QualisDAO(Class<Qualis> objectClass) {
		super(objectClass);
	}

	@Override
	protected Class<Qualis> getEntityKlass() {
		return Qualis.class;
	}

}
