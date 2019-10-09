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
import com.pa.entity.QualisData;
import com.pa.util.EnumPublicationLocalType;


public class QualisDataDatabaseTest {

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
	public void saveQualisDataTest() {
		QualisData qualis = new QualisData("qualis.xls", EnumPublicationLocalType.CONFERENCE, 2015);
		DatabaseFacade.getInstance().saveQualisData(qualis);
		assertNotNull(qualis.getId());
	}
	
	@Test
	public void deleteQualisDataTest() {
		QualisData qAux = DatabaseFacade.getInstance().saveQualisData(new QualisData("qualis.xls", EnumPublicationLocalType.CONFERENCE, 2015));
		DatabaseFacade.getInstance().deleteQualisData(qAux);
		
		QualisData qualisWithId = DatabaseFacade.getInstance().getQualisDataById(qAux.getId());
		
		assertNull(qualisWithId);
	}
	
	@Test
	public void updateQualisDataTest() {
		String name = "toBeUpdated";
		QualisData qualis = new QualisData("qualis.xls", EnumPublicationLocalType.CONFERENCE, 2014);
		qualis.getQualis().add(new Qualis(name, "A2"));
		qualis.getQualis().add(new Qualis(name, "B2"));
		DatabaseFacade.getInstance().saveQualisData(qualis);
		
		QualisData qualisAux = new QualisData("qualis.xls", EnumPublicationLocalType.CONFERENCE, 2014);
		qualisAux.getQualis().add(new Qualis(name, "A1"));
		qualisAux.getQualis().add(new Qualis(name, "B3"));
		qualisAux.getQualis().add(new Qualis("anotherQualis", "B5"));
		DatabaseFacade.getInstance().saveQualisData(qualisAux);
		
		QualisData qWithTypeAndYear = DatabaseFacade.getInstance().getQualisDataByTypeAndYear(EnumPublicationLocalType.CONFERENCE, 2014);
		
		assertEquals(qWithTypeAndYear.getQualis().size(), 3);
	}
	
	@Test
	public void listAllQualisDataTest() {
		QualisData qualis1 = new QualisData("qualis.xls", EnumPublicationLocalType.CONFERENCE, 2014);
		QualisData qualis2 = new QualisData("qualis2.xls", EnumPublicationLocalType.CONFERENCE, 2015);
		DatabaseFacade.getInstance().saveQualisData(qualis1);
		DatabaseFacade.getInstance().saveQualisData(qualis2);
		
		List<QualisData> qualisList = DatabaseFacade.getInstance().listAllQualisData();
		
		assertEquals(qualisList.size(), 2);
	}
	
	@Test
	public void listAllQualisDataByObjectTest() {
		QualisData qualis1 = new QualisData("qualis.xls", EnumPublicationLocalType.CONFERENCE, 2014);
		QualisData qualis2 = new QualisData("qualis2.xls", EnumPublicationLocalType.CONFERENCE, 2015);
		DatabaseFacade.getInstance().saveQualisData(qualis1);
		DatabaseFacade.getInstance().saveQualisData(qualis2);
		
		List<QualisData> List = DatabaseFacade.getInstance().listAllQualisData(qualis1);
		
		assertEquals(List.size(), 1);
	}
	
	@Test
	public void listAllQualisDataByQueryTest() {
		QualisData qualis1 = new QualisData("qualis.xls", EnumPublicationLocalType.CONFERENCE, 2014);
		QualisData qualis2 = new QualisData("qualis2.xls", EnumPublicationLocalType.CONFERENCE, 2015);
		DatabaseFacade.getInstance().saveQualisData(qualis1);
		DatabaseFacade.getInstance().saveQualisData(qualis2);
		
		List<QualisData> pTList = DatabaseFacade.getInstance().listAllQualisDataByQuery("from QualisData");
		
		assertEquals(pTList.size(), 2);
	}
}
