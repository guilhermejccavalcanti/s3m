package com.pa.database.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.pa.database.GenericDAO;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;

class PublicationDAO extends GenericDAO<Publication, Long> {

	public PublicationDAO(Class<Publication> objectClass) {
		super(objectClass);
	}

	@Override
	protected Class<Publication> getEntityKlass() {
		return Publication.class;
	}

	@Override
	public Publication save(Publication x) throws HibernateException {
		
		if(x.getPublicationType() != null && x.getPublicationType().getName()!=null) {
			PublicationTypeDAO pTDAO = new PublicationTypeDAO(PublicationType.class);
			
			PublicationType pT = pTDAO.getPublicationTypeByNameAndType(x.getPublicationType().getName(), x.getPublicationType().getType());
			
			if(pT == null) {
				pT = pTDAO.save(x.getPublicationType());
			}
			
			x.setPublicationType(pT);
		}
		
		return super.save(x);
	}
}
