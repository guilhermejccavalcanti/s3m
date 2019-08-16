package com.pa.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import com.pa.database.util.HibernateUtil;
import com.pa.entity.Qualis;
import com.pa.entity.QualisData;
import com.pa.exception.InvalidPatternFileException;
import com.pa.extractor.QualisExtractor;
import com.pa.util.EnumPublicationLocalType;
import com.pa.util.EnumQualisClassification;

import org.junit.BeforeClass;
import org.junit.Test;

public class QualisPublicationExtractorTest {
	static QualisData data;
	
	@BeforeClass
	public static void getResourceFile() throws InvalidPatternFileException, FileNotFoundException {
		HibernateUtil.createSessionFactory("hibernateTest.cfg.xml");
		
		URL url = QualisPublicationExtractorTest.class.getResource("/publicacoes_2014.xls");
		File publicationFile = new File(url.getFile());
		
		InputStream fileInputStream = new FileInputStream(publicationFile);
		
		QualisExtractor extractor = new QualisExtractor();
		data = extractor.publicationExtractor("2014", fileInputStream, publicationFile.getName());
	}
	
	/*
	 * When extract publication(periodic) Qualis
	 * */
	
	@Test
	public void whenExtractPublicationQualisThenReturnedQualisDataShouldNotBeNull() {
		assertNotNull(data);
	}
	
	@Test
	public void whenExtractPublicationQualisThenQualisDataFileNameMustBeCorrect() {
		assertEquals("publicacoes_2014.xls", data.getFileName());
	}
	
	@Test
	public void whenExtractPublicationQualisThenQualisDataYearMustBeCorrect() {
		assertEquals(new Integer(2014), data.getYear());
	}
	
	@Test
	public void whenExtractPublicationQualisThenQualisDataTypeMustBeCorrect() {
		assertEquals(EnumPublicationLocalType.PERIODIC, data.getType());
	}
	
	@Test
	public void whenExtractPublicationQualisThenQualisDataQualisShouldNotBeEmpty() {
		assertFalse(data.getQualis().isEmpty());
	}
	
	@Test
	public void whenExtractPublicationQualisThenQualisDataQualisShouldHaveCorrectSize() {
		assertEquals(636, data.getQualis().size());
	}
	
	@Test
	public void whenExtractPublicationQualisThenQualisDataQualisShouldHaveCorrectName() {
		Qualis firstQualis = data.getQualis().get(0);
		Qualis lastQualis = data.getQualis().get(data.getQualis().size() - 1);
		Qualis intermediatedQualis = data.getQualis().get(150);
		
		assertEquals("ACM Journal of Experimental Algorithmics", firstQualis.getName());
		assertEquals("iSys: Revista Brasileira de Sistemas de Informação", lastQualis.getName());
		assertEquals("Energies (Basel)", intermediatedQualis.getName());
	}
	
	@Test
	public void whenExtractPublicationQualisThenQualisDataQualisShouldHaveCorrectClassification() {
		Qualis firstQualis = data.getQualis().get(0);
		Qualis lastQualis = data.getQualis().get(data.getQualis().size() - 1);
		Qualis intermediatedQualis = data.getQualis().get(150);
		
		assertEquals(EnumQualisClassification.B4, firstQualis.getClassification());
		assertEquals(EnumQualisClassification.B5, lastQualis.getClassification());
		assertEquals(EnumQualisClassification.B3, intermediatedQualis.getClassification());
	}
	
	/*
	 * When extract invalid file
	 * */
	
	@Test(expected=InvalidPatternFileException.class)
	public void whenExtractInvalidPublicarionQualisThenQualisDataShouldRaiseException() throws InvalidPatternFileException, FileNotFoundException {
		URL url = QualisConferenceExtractorTest.class.getResource("/file_p.xls");
		File publicationFile = new File(url.getFile());
		InputStream fileInputStream = new FileInputStream(publicationFile);
		
		QualisExtractor extractor = new QualisExtractor();
		extractor.publicationExtractor("2013", fileInputStream, publicationFile.getName());
	}
}
