package com.pa.database.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import com.pa.database.GenericDAO;
import com.pa.entity.Curriculo;
import com.pa.entity.Qualis;
import com.pa.entity.QualisData;
import com.pa.util.EnumPublicationLocalType;

class QualisDataDAO extends GenericDAO<QualisData, Long> {

	public QualisDataDAO(Class<QualisData> objectClass) {
		super(objectClass);
	}

	@Override
	protected Class<QualisData> getEntityKlass() {
		return QualisData.class;
	}
	
	public QualisData getQualisDataByTypeAndYear(EnumPublicationLocalType type, int year) {
		Criteria criteria = super.createCriteria(QualisData.class);
		
		criteria.add(Restrictions.eq("type", type));
		criteria.add(Restrictions.eq("year", year));

		QualisData qualisData = (QualisData) criteria.uniqueResult();
		
		return qualisData;
	}
	
	@Override
	public void update(QualisData x) throws HibernateException {
		if(x.getId() != null) {
			super.merge(x);
		}
		else {
			save(x);
		}
	}
	
	@Override
	public QualisData save(QualisData x) throws HibernateException {
		QualisData qDfromDB = getQualisDataByTypeAndYear(x.getType(), x.getYear());
		
		if(qDfromDB != null) {
			super.delete(qDfromDB);
			return super.save(x);
		}
		else {
			return super.save(x);
		}
	}
	
//	@Override
//	public QualisData save(QualisData x) throws HibernateException {
//		QualisDAO qDAO = new QualisDAO(Qualis.class);
//		
//		if(getQualisDataByTypeAndYear(x.getType(), x.getYear()) == null) {
//			for (Qualis qualis : x.getQualis()) {
//				qDAO.save(qualis);
//			}
//		}
//		else {
//			throw new HibernateException("Já há qualis para " + x.getType() + " do ano " +  x.getYear() + " cadastrado no banco.", cause)
//		}
//		
//		return super.save(x);
//	}

}
