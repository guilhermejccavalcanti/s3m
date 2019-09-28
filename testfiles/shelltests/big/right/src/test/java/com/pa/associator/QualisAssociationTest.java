package com.pa.associator;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pa.associator.QualisAssociatorService;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.entity.Qualis;
import com.pa.entity.QualisData;
import com.pa.util.EnumPublicationLocalType;
import com.pa.util.EnumQualisClassification;

public class QualisAssociationTest {

	private static final EnumPublicationLocalType LOCAL_TYPE = EnumPublicationLocalType.CONFERENCE;
	private static final int YEAR_OF_QUALIS = 2015;
	private static final String NAME_OF_QUALIS = "Conferencia de Teste";
	
	private static QualisAssociatorService qService = null;
	
	@BeforeClass
	public static void getQualisServiceInstance() {
		qService = QualisAssociatorService.getInstance();
	}
	
	@Test
	public void associateExistentQualisToPublicationTest() {
		QualisData qualis = new QualisData("qualis.xls", LOCAL_TYPE, YEAR_OF_QUALIS);
		qualis.getQualis().add(new Qualis(NAME_OF_QUALIS, "A2"));
		
		Publication publication = new Publication("Publicação de Teste", YEAR_OF_QUALIS, new PublicationType(NAME_OF_QUALIS, LOCAL_TYPE), null);
		
		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(LOCAL_TYPE, qualis);
		qService.associatePublicationQualis(publication, qualisDataMap);
		
		assertEquals(publication.getQualis(), EnumQualisClassification.A2);
	}
	
	@Test
	public void associateInexistentQualisToPublicationTest() {
		QualisData qualis = new QualisData("qualis.xls", LOCAL_TYPE, YEAR_OF_QUALIS);
		qualis.getQualis().add(new Qualis(NAME_OF_QUALIS, "A2"));
		
		Publication publication = new Publication("Publicação de Teste", YEAR_OF_QUALIS, new PublicationType("Tipo de Publicação", LOCAL_TYPE), null);
		
		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(LOCAL_TYPE, qualis);
		qService.associatePublicationQualis(publication, qualisDataMap);
		
		assertEquals( EnumQualisClassification.NONE, publication.getQualis());
	}
	
	@Test
	public void associatePublicationToAQualisFromAnotherYearTest() {
		QualisData qualis = new QualisData("qualis.xls", LOCAL_TYPE, YEAR_OF_QUALIS);
		qualis.getQualis().add(new Qualis(NAME_OF_QUALIS, "A2"));
		
		Publication publication = new Publication("Publicação de Teste", 2014, new PublicationType(NAME_OF_QUALIS, LOCAL_TYPE), null);

		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(LOCAL_TYPE, qualis);
		qService.associatePublicationQualis(publication, qualisDataMap);
		
		assertEquals(EnumQualisClassification.A2, publication.getQualis());
	}
	
	@Test
	public void associateInexistentQualisEmptyToPublicationTest() {
		Publication publication = new Publication("Publicação de Teste", YEAR_OF_QUALIS, new PublicationType("Tipo de Publicação", LOCAL_TYPE), null);
		qService.associatePublicationQualis(publication, null);
		
		assertEquals(publication.getQualis(), EnumQualisClassification.NONE);
	}
	
	@Test
	public void associateQualisToPublicationUpdateButNotAssociateTest() {
		QualisData qualis = new QualisData("qualis.xls", LOCAL_TYPE, YEAR_OF_QUALIS);
		qualis.getQualis().add(new Qualis(NAME_OF_QUALIS, "A2"));
		
		Publication publication = new Publication("Publicação de Teste", YEAR_OF_QUALIS, new PublicationType(NAME_OF_QUALIS, LOCAL_TYPE), null);
		
		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(LOCAL_TYPE, qualis);
		qService.associatePublicationQualis(publication, qualisDataMap);
		
		qualis.getQualis().get(0).setClassification(EnumQualisClassification.C);
		
		assertEquals(publication.getQualis(), EnumQualisClassification.A2);
	}
	
	@Test
	public void associateQualisToPublicationUpdateAndAssociateTest() {
		QualisData qualis = new QualisData("qualis.xls", LOCAL_TYPE, YEAR_OF_QUALIS);
		qualis.getQualis().add(new Qualis(NAME_OF_QUALIS, "A2"));
		
		Publication publication = new Publication("Publicação de Teste", YEAR_OF_QUALIS, new PublicationType(NAME_OF_QUALIS, LOCAL_TYPE), null);
		
		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(LOCAL_TYPE, qualis);
		qService.associatePublicationQualis(publication, qualisDataMap);
		
		qualis.getQualis().get(0).setClassification(EnumQualisClassification.C);
		qService.associatePublicationQualis(publication, qualisDataMap);
		
		assertEquals(publication.getQualis(), EnumQualisClassification.C);
	}
	
	@Test
	public void associateQualisToPublicationWithDifferentPublicationLocalTypesTest() {
		QualisData qualis = new QualisData("qualis.xls", LOCAL_TYPE, YEAR_OF_QUALIS);
		qualis.getQualis().add(new Qualis(NAME_OF_QUALIS, "A2"));
		
		QualisData qualisPeriodic = new QualisData("qualisPeriodic.xls", EnumPublicationLocalType.PERIODIC, YEAR_OF_QUALIS);
		String periodicName = "Periodico Teste";
		qualisPeriodic.getQualis().add(new Qualis(periodicName, "C"));
		
		Publication publication = new Publication("Publicação de Teste", YEAR_OF_QUALIS, new PublicationType(periodicName, EnumPublicationLocalType.PERIODIC), null);
		
		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(LOCAL_TYPE, qualis);
		qualisDataMap.put(EnumPublicationLocalType.PERIODIC, qualisPeriodic);
		qService.associatePublicationQualis(publication, qualisDataMap);
		
		assertEquals(EnumQualisClassification.C, publication.getQualis());
	}
	
	@Test
	public void associateInexistentQualisDataTypeToAPublicationTest() {
		QualisData qualis = new QualisData("qualis.xls", LOCAL_TYPE, YEAR_OF_QUALIS);
		qualis.getQualis().add(new Qualis(NAME_OF_QUALIS, "A2"));
		
		Publication publication = new Publication("Publicação de Teste", YEAR_OF_QUALIS, new PublicationType("Periodico Teste", EnumPublicationLocalType.PERIODIC), null);
		
		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(LOCAL_TYPE, qualis);
		qService.associatePublicationQualis(publication, qualisDataMap);
		
		assertEquals(EnumQualisClassification.NONE, publication.getQualis());
	}
}
