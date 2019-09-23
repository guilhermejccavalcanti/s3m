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
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.util.EnumPublicationLocalType;

public class PublicationDatabaseTest {

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
	public void savePublicationTest() {
		Publication publication = new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null);
		DatabaseFacade.getInstance().savePublication(publication);
		assertNotNull(publication.getId());
	}
	
	@Test
	public void deletePublicationTest() {
		Publication publication = new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null);
		DatabaseFacade.getInstance().savePublication(publication);
		DatabaseFacade.getInstance().deletePublication(publication);
		
		Publication publicationWithId = DatabaseFacade.getInstance().getPublicationById(publication.getId());
		
		assertNull(publicationWithId);
	}
	
	@Test
	public void deletePublicationAndCheckPublicationTypeTest() {
		PublicationType typePublication = new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE);
		Publication publication = new Publication("Publication1", 2015, typePublication, null);
		DatabaseFacade.getInstance().savePublication(publication);
		DatabaseFacade.getInstance().deletePublication(publication);
		
		PublicationType publicationTypeWithId = DatabaseFacade.getInstance().getPublicationTypeByNameAndType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE);
		
		assertNotNull(publicationTypeWithId);
	}
	
	@Test
	public void updatePublicationTest() {
		PublicationType typePublication = new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE);
		Publication publication = new Publication("Publication1", 2015, typePublication, null);
		DatabaseFacade.getInstance().savePublication(publication);
		
		publication.setTitle("TipoDePublicacaoEditada");
		DatabaseFacade.getInstance().updatePublication(publication);
		
		assertEquals(publication.getTitle(), "TipoDePublicacaoEditada");
	}
	
	@Test
	public void listAllPublicationTest() {
		Publication publication = new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null);
		Publication publication2 = new Publication("Publication2", 2015, new PublicationType("TipoDePublicacao2", EnumPublicationLocalType.CONFERENCE), null);
		DatabaseFacade.getInstance().savePublication(publication);
		DatabaseFacade.getInstance().savePublication(publication2);
		
		List<Publication> qualisList = DatabaseFacade.getInstance().listAllPublications();
		
		assertEquals(qualisList.size(), 2);
	}
	
	@Test
	public void listAllPublicationByObjectTest() {
		Publication publication = new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null);
		Publication publication2 = new Publication("Publication2", 2015, new PublicationType("TipoDePublicacao2", EnumPublicationLocalType.CONFERENCE), null);
		DatabaseFacade.getInstance().savePublication(publication);
		DatabaseFacade.getInstance().savePublication(publication2);
		
		List<Publication> list = DatabaseFacade.getInstance().listAllPublications(publication);
		
		assertEquals(list.size(), 1);
	}
	
	@Test
	public void listAllPublicationByQueryTest() {
		Publication publication = new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null);
		Publication publication2 = new Publication("Publication2", 2015, new PublicationType("TipoDePublicacao2", EnumPublicationLocalType.CONFERENCE), null);
		DatabaseFacade.getInstance().savePublication(publication);
		DatabaseFacade.getInstance().savePublication(publication2);
		
		List<Publication> pTList = DatabaseFacade.getInstance().listAllPublicationsByQuery("from Publication");
		
		assertEquals(pTList.size(), 2);
	}
}
