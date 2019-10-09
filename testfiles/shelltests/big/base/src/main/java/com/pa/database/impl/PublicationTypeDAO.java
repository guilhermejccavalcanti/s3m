package com.pa.database.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import com.pa.database.GenericDAO;
import com.pa.entity.PublicationType;
import com.pa.util.EnumPublicationLocalType;

class PublicationTypeDAO extends GenericDAO<PublicationType, Long> {

	public PublicationTypeDAO(Class<PublicationType> objectClass) {
		super(objectClass);
	}

	@Override
	protected Class<PublicationType> getEntityKlass() {
		return PublicationType.class;
	}
	
	public PublicationType getPublicationTypeByNameAndType(String name, EnumPublicationLocalType type) throws HibernateException {

		Criteria criteria = createCriteria(PublicationType.class);
		
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("type", type));

		PublicationType publicationType = (PublicationType) criteria.uniqueResult();
		return publicationType;
	}

}
