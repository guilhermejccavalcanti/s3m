package com.pa.database.impl;

import org.hibernate.HibernateException;

import com.pa.database.GenericDAO;
import com.pa.entity.TechnicalProduction;

public class TechinicalProductionDAO extends GenericDAO<TechnicalProduction, Long>{

	public TechinicalProductionDAO(Class<TechnicalProduction> objectClass) {
		super(objectClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Class<TechnicalProduction> getEntityKlass() {
		return TechnicalProduction.class;
	}
	
	@Override
	public TechnicalProduction save(TechnicalProduction x) throws HibernateException {
		return super.save(x);
	}

}
