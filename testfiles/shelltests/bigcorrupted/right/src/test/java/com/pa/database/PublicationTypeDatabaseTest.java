package com.pa.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pa.database.impl.DatabaseFacade;
import com.pa.database.util.HibernateUtil;
import com.pa.entity.PublicationType;
import com.pa.util.EnumPublicationLocalType;


public class PublicationTypeDatabaseTest {

	@Before
	public void createSessionFactory() {
		HibernateUtil.createSessionFactory("hibernateTest.cfg.xml");
	}
	
	@Before
	public void createPublicationType() {
		
	}
	
	@After
	public void closeSession() {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		
		if(sessionFactory.getCurrentSession().isOpen()) {
			sessionFactory.getCurrentSession().close();
		}
		
		if(!sessionFactory.isClosed()) {
			sessionFactory.close();
		}
	}
	
	@Test()
	public void savePublicationTypeTest() {
		PublicationType pType = new PublicationType("name", EnumPublicationLocalType.CONFERENCE);
		DatabaseFacade.getInstance().savePublicationType(pType);
		assertNotNull(pType.getIdentifier());
	}
	
	
	@Test
	public void getPublicationTypeByNameTest() {
		String name = "publicationType";
		EnumPublicationLocalType type = EnumPublicationLocalType.PERIODIC;
		
		DatabaseFacade.getInstance().savePublicationType(new PublicationType(name, type));
		PublicationType pTAux = DatabaseFacade.getInstance().getPublicationTypeByNameAndType(name, type);
		
		assertNotNull(pTAux);
	}
	
	@Test
	public void deletePublicationTypeByNameTest() {
		String name = "toBeDeleted";
		
		PublicationType pTAux = DatabaseFacade.getInstance().savePublicationType(new PublicationType(name, EnumPublicationLocalType.PERIODIC));
		DatabaseFacade.getInstance().deletePublicationType(pTAux);
		
		PublicationType pTWithId = DatabaseFacade.getInstance().getPublicationTypeById(pTAux.getIdentifier());
		
		assertNull(pTWithId);
	}
	
	@Test(expected=HibernateException.class)
	public void saveDuplicatedPublicationTypeTest() {
		DatabaseFacade.getInstance().savePublicationType(new PublicationType("nameTest", EnumPublicationLocalType.CONFERENCE));
		DatabaseFacade.getInstance().savePublicationType(new PublicationType("nameTest", EnumPublicationLocalType.CONFERENCE)); //must throws an Hibernate exception
	}
	
	@Test
	public void updatePublicationTypeTest() {
		String name = "toBeUpdated";
		PublicationType pT = new PublicationType(name, EnumPublicationLocalType.PERIODIC);
		
		DatabaseFacade.getInstance().savePublicationType(pT);
		
		pT.setType(EnumPublicationLocalType.CONFERENCE);
		DatabaseFacade.getInstance().updatePublicationType(pT);
		
		PublicationType pTWithId = DatabaseFacade.getInstance().getPublicationTypeById(pT.getIdentifier());
		
		assertEquals(pTWithId.getType(), EnumPublicationLocalType.CONFERENCE);
	}
	
	@Test
	public void listAllPublicationTypeTest() {
		PublicationType pT1 = new PublicationType("nameTest1", EnumPublicationLocalType.CONFERENCE);
		PublicationType pT2 = new PublicationType("nameTest2", EnumPublicationLocalType.PERIODIC);
		DatabaseFacade.getInstance().savePublicationType(pT1);
		DatabaseFacade.getInstance().savePublicationType(pT2);
		
		List<PublicationType> pTList = DatabaseFacade.getInstance().listAllPublicationTypes();
		
		assertEquals(pTList.size(), 2);
	}
	
	@Test
	public void listAllPublicationTypeByObjectTest() {
		PublicationType pT1 = new PublicationType("nameTest1", EnumPublicationLocalType.CONFERENCE);
		DatabaseFacade.getInstance().savePublicationType(pT1);
		
		List<PublicationType> pTList = DatabaseFacade.getInstance().listAllPublicationTypes(pT1);
		
		assertEquals(pTList.size(), 1);
	}
	
	@Test
	public void listAllPublicationTypeByQueryTest() {
		PublicationType pT1 = new PublicationType("nameTest1", EnumPublicationLocalType.CONFERENCE);
		PublicationType pT2 = new PublicationType("nameTest2", EnumPublicationLocalType.PERIODIC);
		DatabaseFacade.getInstance().savePublicationType(pT1);
		DatabaseFacade.getInstance().savePublicationType(pT2);
		
		List<PublicationType> pTList = DatabaseFacade.getInstance().listAllPublicationTypesByQuery("from PublicationType");
		
		assertEquals(pTList.size(), 2);
	}
}
