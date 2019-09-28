package com.pa.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pa.database.impl.DatabaseFacade;
import com.pa.database.util.HibernateUtil;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.util.EnumPublicationLocalType;

public class GroupDatabaseTest {


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
	public void saveCurriculoTest() {
		Group group = new Group("UFPE");
		Curriculo curriculo = new Curriculo("Curriculo1", new Date());
		curriculo.setId(10256l);
		
		group.getCurriculos().add(curriculo);
		DatabaseFacade.getInstance().saveGroup(group);
		assertNotNull(group.getId());
	}
	
	@Test
	public void saveCurriculoWithPublicationTest() {
		Group group = new Group("UFPE");
		Curriculo curriculo = new Curriculo("Curriculo1", new Date());
		curriculo.setId(10256l);
		
		group.getCurriculos().add(curriculo);
		DatabaseFacade.getInstance().saveGroup(group);
		
		List<Curriculo> publications = DatabaseFacade.getInstance().listAllCurriculos();
		
		assertEquals(publications.size(), 1);
	}
	
	@Test
	public void deleteGroupTest() {
		Group group = new Group("UFPE");
		Curriculo curriculo = new Curriculo("Curriculo1", new Date());
		curriculo.setId(10256l);
		
		group.getCurriculos().add(curriculo);
		DatabaseFacade.getInstance().saveGroup(group);
		DatabaseFacade.getInstance().deleteGroup(group);
		
		Group groupBD = DatabaseFacade.getInstance().getGroupById(group.getId());
		
		assertNull(groupBD);
	}
	
	@Test
	public void deleteGroupAndCheckCurriculoTest() {
		Group group = new Group("UFPE");
		Curriculo curriculo = new Curriculo("Curriculo1", new Date());
		curriculo.setId(10256l);
		
		group.getCurriculos().add(curriculo);
		DatabaseFacade.getInstance().saveGroup(group);
		DatabaseFacade.getInstance().deleteGroup(group);
		
		Curriculo curriculoBD = DatabaseFacade.getInstance().getCurriculoByName("Curriculo1");
		
		assertNotNull(curriculoBD);
	}
	
	@Test
	public void updateGroupTest() {
		Group group = new Group("UFPE");
		Curriculo curriculo = new Curriculo("Curriculo1", new Date());
		curriculo.setId(10256l);
		curriculo.getPublications().add(new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null));
		group.getCurriculos().add(curriculo);
		DatabaseFacade.getInstance().saveGroup(group);
		
		group.setName("UFPB");
		DatabaseFacade.getInstance().updateGroup(group);
		
		assertEquals(group.getName(), "UFPB");
	}
	
	@Test
	public void listAllGroupTest() {
		Group group = new Group("UFPE");
		Curriculo curriculo = new Curriculo("Curriculo1", new Date());
		curriculo.setId(10256l);
		curriculo.getPublications().add(new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null));
		group.getCurriculos().add(curriculo);
		
		Group group2 = new Group("UFPB");
		Curriculo curriculo2 = new Curriculo("Curriculo2", new Date());
		curriculo2.setId(10257l);
		curriculo2.getPublications().add(new Publication("Publication2", 2015, new PublicationType("TipoDePublicacao2", EnumPublicationLocalType.CONFERENCE), null));
		group2.getCurriculos().add(curriculo2);
		
		DatabaseFacade.getInstance().saveGroup(group);
		DatabaseFacade.getInstance().saveGroup(group2);
		
		List<Group> groupList = DatabaseFacade.getInstance().listAllGroups();
		
		assertEquals(groupList.size(), 2);
	}
	
	@Test
	public void listAllGroupByObjectTest() {
		Group group = new Group("UFPE");
		Curriculo curriculo = new Curriculo("Curriculo1", new Date());
		curriculo.setId(10256l);
		curriculo.getPublications().add(new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null));
		group.getCurriculos().add(curriculo);
		
		Group group2 = new Group("UFPB");
		Curriculo curriculo2 = new Curriculo("Curriculo2", new Date());
		curriculo2.setId(10257l);
		curriculo2.getPublications().add(new Publication("Publication2", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null));
		group2.getCurriculos().add(curriculo2);
		
		DatabaseFacade.getInstance().saveGroup(group);
		DatabaseFacade.getInstance().saveGroup(group2);
		
		List<Group> list = DatabaseFacade.getInstance().listAllGroups(group);
		
		assertEquals(list.size(), 1);
	}
	
	@Test
	public void listAllGroupByQueryTest() {
		Group group = new Group("UFPE");
		Curriculo curriculo = new Curriculo("Curriculo1", new Date());
		curriculo.setId(10256l);
		curriculo.getPublications().add(new Publication("Publication1", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null));
		group.getCurriculos().add(curriculo);
		
		Group group2 = new Group("UFPB");
		Curriculo curriculo2 = new Curriculo("Curriculo2", new Date());
		curriculo2.setId(10257l);
		curriculo2.getPublications().add(new Publication("Publication2", 2015, new PublicationType("TipoDePublicacao1", EnumPublicationLocalType.CONFERENCE), null));
		group2.getCurriculos().add(curriculo2);
		
		DatabaseFacade.getInstance().saveGroup(group);
		DatabaseFacade.getInstance().saveGroup(group2);
		
		List<Group> pTList = DatabaseFacade.getInstance().listAllGroupsByQuery("from Group");
		
		assertEquals(pTList.size(), 2);
	}
	
}
