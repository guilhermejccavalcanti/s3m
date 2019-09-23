package com.pa.database;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.entity.Qualis;
import com.pa.entity.QualisData;
import com.pa.util.EnumPublicationLocalType;


public class PersistTest {

	@Before
	public void setUp() throws Exception {
		
		
	}

	@Test
	public void test() {
		Group grupo1 = new Group("Grupo 3");
		
		
		Curriculo lattes = new Curriculo();
		lattes.setLastUpdate(new Date());
		lattes.setCountConcludedOrientations(5);
		lattes.setCountOnGoingOrientations(5);
		lattes.setName("ABC xxxxx");
		lattes.setId(27l);
		
		Publication publication = new Publication();
		publication.setTitle("Publicacao31");
		publication.setYear(2014);
		
		PublicationType publicationType = new PublicationType();
		publicationType.setName("Article");
		publicationType.setType(EnumPublicationLocalType.CONFERENCE);
		
//		PublicationTypeDAO pTDao = new PublicationTypeDAO(PublicationType.class);
//		pTDao.save(publicationType);
		
		publication.setPublicationType(publicationType);
		
		Set<Publication> publications = new HashSet<Publication>();
		publications.add(publication);
		
//		PublicationDAO pDao = new PublicationDAO(Publication.class);
//		pDao.save(publication);
		
		lattes.setPublications(publications);
		
		grupo1.getCurriculos().add(lattes);
		
//		CurriculoDAO cDao = new CurriculoDAO(Curriculo.class);
//		cDao.save(lattes);
		
		DatabaseFacade.getInstance().saveGroup(grupo1);
		
		
//		QualisData qD = new QualisData("file", EnumPublicationLocalType.CONFERENCE, 2014);
//		Qualis  qualis = new Qualis("Article", "A1");
//		
//		qD.getQualis().add(qualis);
//		DatabaseFacade.getInstance().saveQualisData(qD);
//		
//		QualisData qD2 = new QualisData("file", EnumPublicationLocalType.PERIODIC, 2015);
//		Qualis  qualis2 = new Qualis("Article2", "A3");
//		
//		qD2.getQualis().add(qualis2);
//		DatabaseFacade.getInstance().saveQualisData(qD2);
		
	}
}
