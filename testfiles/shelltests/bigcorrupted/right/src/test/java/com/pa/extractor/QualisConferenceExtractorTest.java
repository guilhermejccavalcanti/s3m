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

public class QualisConferenceExtractorTest {
	static QualisData data;
	
	@BeforeClass
	public static void getResourceFile() throws InvalidPatternFileException, FileNotFoundException {
		HibernateUtil.createSessionFactory("hibernateTest.cfg.xml");
		
		URL url = QualisConferenceExtractorTest.class.getResource("/conferencia_2012.xlsx");
		File conferenceFile = new File(url.getFile());
		
		InputStream fileInputStream = new FileInputStream(conferenceFile);
		
		QualisExtractor extractor = new QualisExtractor();		
		data = extractor.conferenceExtractor("2012", fileInputStream, conferenceFile.getName());
	}
	
	/*
	 * When extract conference Qualis
	 * */
	
	@Test
	public void whenExtractConferenceQualisThenReturnedQualisDataShouldNotBeNull() {
		assertNotNull(data);
	}
	
	@Test
	public void whenExtractConferenceQualisThenQualisDataFileNameMustBeCorrect() {
		assertEquals("conferencia_2012.xlsx", data.getFileName());
	}
	
	@Test
	public void whenExtractConferenceQualisThenQualisDataYearMustBeCorrect() {
		assertEquals(new Integer(2012), data.getYear());
	}
	
	@Test
	public void whenExtractConferenceQualisThenQualisDataTypeMustBeCorrect() {
		assertEquals(EnumPublicationLocalType.CONFERENCE, data.getType());
	}
	
	@Test
	public void whenExtractConferenceQualisThenQualisDataQualisShouldNotBeEmpty() {
		assertFalse(data.getQualis().isEmpty());
	}
	
	@Test
	public void whenExtractConferenceQualisThenQualisDataQualisShouldHaveCorrectSize() {
		assertEquals(1703, data.getQualis().size());
	}
	
	@Test
	public void whenExtractConferenceQualisThenQualisDataQualisShouldHaveCorrectName() {
		Qualis firstQualis = data.getQualis().get(0);
		Qualis lastQualis = data.getQualis().get(data.getQualis().size() - 1);
		Qualis intermediatedQualis = data.getQualis().get(150);
		
		assertEquals("Annual Joint Conference of the IEEE Computer and Communications Societies", firstQualis.getName());
		assertEquals("Brazilian Workshop on Semantic Web and Education", lastQualis.getName());
		assertEquals("International Conference on Language Resources and Evaluation", intermediatedQualis.getName());
	}
	
	@Test
	public void whenExtractConferenceQualisThenQualisDataQualisShouldHaveCorrectClassification() {
		Qualis firstQualis = data.getQualis().get(0);
		Qualis lastQualis = data.getQualis().get(data.getQualis().size() - 1);
		Qualis intermediatedQualis = data.getQualis().get(150);
		
		assertEquals(EnumQualisClassification.A1, firstQualis.getClassification());
		assertEquals(EnumQualisClassification.B5, lastQualis.getClassification());
		assertEquals(EnumQualisClassification.A2, intermediatedQualis.getClassification());
	}
	
	/*
	 * When extract invalid file
	 * */
	
	@Test(expected=InvalidPatternFileException.class)
	public void whenExtractInvalidConferenceQualisThenQualisDataShouldRaiseException() throws InvalidPatternFileException, FileNotFoundException {
		URL url = QualisConferenceExtractorTest.class.getResource("/file.xlsx");
		File conferenceFile = new File(url.getFile());
		InputStream fileInputStream;
		
		fileInputStream = new FileInputStream(conferenceFile);
	
		QualisExtractor extractor = new QualisExtractor();
		extractor.conferenceExtractor("2014", fileInputStream, conferenceFile.getName());		
	}
}
