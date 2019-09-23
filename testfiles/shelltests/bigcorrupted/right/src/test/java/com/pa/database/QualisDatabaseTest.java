package com.pa.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pa.database.impl.DatabaseFacade;
import com.pa.database.util.HibernateUtil;
import com.pa.entity.Qualis;
import com.pa.util.EnumQualisClassification;


public class QualisDatabaseTest {
	
	@Before
	public void createSessionFactory() {
		HibernateUtil.createSessionFactory("hibernateTest.cfg.xml");
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
	
	@Test
	public void savePublicationTypeTest() {
		Qualis qualis = new Qualis("name", "A1");
		DatabaseFacade.getInstance().saveQualis(qualis);
		assertNotNull(qualis.getId());
	}
	
	@Test
	public void deletePublicationTypeByNameTest() {
		String name = "name";
		
		Qualis qAux = DatabaseFacade.getInstance().saveQualis(new Qualis(name, "A2"));
		DatabaseFacade.getInstance().deleteQualis(qAux);
		
		Qualis qualisWithId = DatabaseFacade.getInstance().getQualisById(qAux.getId());
		
		assertNull(qualisWithId);
	}
	
	@Test
	public void updatePublicationTypeTest() {
		String name = "toBeUpdated";
		Qualis qualis = new Qualis(name, "A1");
		
		DatabaseFacade.getInstance().saveQualis(qualis);
		
		qualis.setClassification(EnumQualisClassification.B2);
		DatabaseFacade.getInstance().updateQualis(qualis);
		
		Qualis qWithId = DatabaseFacade.getInstance().getQualisById(qualis.getId());
		
		assertEquals(qWithId.getClassification(), EnumQualisClassification.B2);
	}
	
	@Test
	public void listAllPublicationTypeTest() {
		Qualis qualis1 = new Qualis("nameTest1", "A1");
		Qualis qualis2 = new Qualis("nameTest2", "C");
		DatabaseFacade.getInstance().saveQualis(qualis1);
		DatabaseFacade.getInstance().saveQualis(qualis2);
		
		List<Qualis> qualisList = DatabaseFacade.getInstance().listAllQualis();
		
		assertEquals(qualisList.size(), 2);
	}
	
	@Test
	public void listAllPublicationTypeByObjectTest() {
		Qualis qualis1 = new Qualis("nameTest1", "A1");
		DatabaseFacade.getInstance().saveQualis(qualis1);
		
		List<Qualis> List = DatabaseFacade.getInstance().listAllQualis(qualis1);
		
		assertEquals(List.size(), 1);
	}
	
	@Test
	public void listAllPublicationTypeByQueryTest() {
		Qualis qualis1 = new Qualis("nameTest1", "A1");
		Qualis qualis2 = new Qualis("nameTest1", "C");
		DatabaseFacade.getInstance().saveQualis(qualis1);
		DatabaseFacade.getInstance().saveQualis(qualis2);
		
		List<Qualis> pTList = DatabaseFacade.getInstance().listAllQualisByQuery("from Qualis");
		
		assertEquals(pTList.size(), 2);
	}
}
