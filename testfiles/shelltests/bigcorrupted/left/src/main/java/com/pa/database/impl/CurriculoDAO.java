package com.pa.database.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import com.pa.database.GenericDAO;
import com.pa.entity.Curriculo;
import com.pa.entity.Publication;

class CurriculoDAO extends GenericDAO<Curriculo, Long> {

	public CurriculoDAO(Class<Curriculo> objectClass) {
		super(objectClass);
	}
	

	@Override
	public Curriculo save(Curriculo x) throws HibernateException {
		PublicationDAO pDAO = new PublicationDAO(Publication.class);
		
		if(x.getPublications()!=null && !x.getPublications().isEmpty()) {
			for (Publication publication : x.getPublications()) {
				pDAO.save(publication);
			}
		}
		
		return super.save(x);
	}
	
	public Curriculo getCurriculoByName(String name) throws HibernateException {

		Criteria criteria = createCriteria(Curriculo.class);

		criteria.add(Restrictions.eq("name", name));

		Curriculo curriculo = (Curriculo) criteria.uniqueResult();
		
		return curriculo;
	}
	
	@Override
	protected Class<Curriculo> getEntityKlass() {
		return Curriculo.class;
	}

}
