package com.pa.database.impl;

import org.hibernate.HibernateException;

import com.pa.database.GenericDAO;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;

class GroupDAO extends GenericDAO<Group, Long> {

	public GroupDAO(Class<Group> objectClass) {
		super(objectClass);
	}

	@Override
	protected Class<Group> getEntityKlass() {
		return Group.class;
	}

	@Override
	public Group save(Group x) throws HibernateException {
		CurriculoDAO cDAO = new CurriculoDAO(Curriculo.class);
		
		for (Curriculo curriculo : x.getCurriculos()) {
			Curriculo cLattesBD = cDAO.get(curriculo.getId());
			
			if(cLattesBD == null) {
				cDAO.save(curriculo);
			}
		}
		
		return super.save(x);
	}
}
