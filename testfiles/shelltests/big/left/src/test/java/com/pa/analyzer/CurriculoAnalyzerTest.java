package com.pa.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pa.analyzer.CurriculoAnalyzer;
import com.pa.analyzer.CurriculoResult;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pa.database.impl.DatabaseFacade;
import com.pa.database.util.HibernateUtil;
import com.pa.entity.Curriculo;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.entity.Qualis;
import com.pa.entity.QualisData;
import com.pa.util.EnumPublicationLocalType;
import com.pa.util.EnumQualisClassification;

public class CurriculoAnalyzerTest {

	private static Map<EnumPublicationLocalType, QualisData> qualisDataMap;
	
	@BeforeClass
	public static void createQualis() {
		QualisData qualisData = new QualisData("qualis.xls", EnumPublicationLocalType.CONFERENCE, 2015);
		qualisData.getQualis().add(new Qualis("Conferencia X", "A1"));
		qualisData.getQualis().add(new Qualis("Conferencia Y", "A1"));
		
		qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(EnumPublicationLocalType.CONFERENCE, qualisData);
	}
	
	@Test
	public void analyseCurriculoQualisTest() {
		Curriculo c = new Curriculo("Nome", new Date());
		
		PublicationType typePublication = new PublicationType("Conferencia X", EnumPublicationLocalType.CONFERENCE);
		Publication publication = new Publication("Primeira Publicacao A1", 2015, typePublication, null);
		PublicationType typePublication2 = new PublicationType("Conferencia Y", EnumPublicationLocalType.CONFERENCE);
		Publication publication2 = new Publication("Segunda Publicacao A1", 2015, typePublication2, null);
	
		c.getPublications().add(publication);
		c.getPublications().add(publication2);
		
		CurriculoResult cR = CurriculoAnalyzer.getInstance().analyzerCurriculo(c, qualisDataMap);
		Map<EnumQualisClassification, List<Publication>> mapPublicationsByQualis = cR.getPeriodicsByQualis();
		//int qualisA1 = mapPublicationsByQualis.get(EnumQualisClassification.A1).size();
		
		//assertEquals(2, qualisA1);
	}
	
	@Test
	public void analyseCurriculoConcludedOrientationsTest() {
		Curriculo c = new Curriculo("Nome", new Date());
		c.setCountConcludedOrientations(3);
		
		CurriculoResult cR = CurriculoAnalyzer.getInstance().analyzerCurriculo(c, qualisDataMap);
		int concludedOrientation = cR.getConcludedOrientations();
		
		assertEquals(3, concludedOrientation);
	}
	
	@Test
	public void analyseCurriculoOnGoingOrientationsTest() {
		Curriculo c = new Curriculo("Nome", new Date());
		c.setCountOnGoingOrientations(2);
		
		CurriculoResult cR = CurriculoAnalyzer.getInstance().analyzerCurriculo(c, qualisDataMap);
		int onGoingOrientations = cR.getOnGoingOrientations();
		
		assertEquals(2, onGoingOrientations);
	}
	
	@Test
	public void analyseCurriculoEmptyTest() {
		Curriculo c = new Curriculo("Nome", new Date());
		
		CurriculoResult cR = CurriculoAnalyzer.getInstance().analyzerCurriculo(c, qualisDataMap);
		int concludedOrientation = cR.getConcludedOrientations();
		
		assertEquals(0, concludedOrientation);
	}
	
	@Test
	public void analyseQualisPublicationsInEmptyCurriculoTest() {
		Curriculo c = new Curriculo("Nome", new Date());
		
		CurriculoResult cR = CurriculoAnalyzer.getInstance().analyzerCurriculo(c, qualisDataMap);
		List<Publication> publications = cR.getPeriodicsByQualis().get(EnumQualisClassification.A1);
		
		assertNull(publications);
	}
	
	@Test
	public void analyseNullCurriculoTest() {
		CurriculoResult cR = CurriculoAnalyzer.getInstance().analyzerCurriculo(null, qualisDataMap);
		
		assertNull(cR);
	}
}
