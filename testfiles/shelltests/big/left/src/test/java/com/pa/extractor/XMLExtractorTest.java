package com.pa.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.pa.database.util.HibernateUtil;
import com.pa.entity.Curriculo;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.exception.InvalidPatternFileException;
import com.pa.extractor.XMLExtractor;
import com.pa.util.EnumPublicationLocalType;

import org.junit.BeforeClass;
import org.junit.Test;

public class XMLExtractorTest {
	static Curriculo curriculo;
	
	static Publication publicationConference;
	static Publication publicationPeriodic;
	
	static PublicationType typeConference;
	static PublicationType typePeriodic;
	
	@BeforeClass
	public static void getResources() throws InvalidPatternFileException, FileNotFoundException {
		HibernateUtil.createSessionFactory("hibernateTest.cfg.xml");
		
		URL url = MultipleXMLExtractorTest.class.getResource("/multipleXML/Fernando.xml");
		File curriculoFile = new File(url.getFile());
		
		XMLExtractor extractor = new XMLExtractor();
		curriculo = extractor.lattesExtractor(new FileInputStream(curriculoFile));
		
		for (Iterator<Publication> it = curriculo.getPublications().iterator(); it.hasNext();) {
			Publication next = it.next();
			
			if (next.getTitle().equals("Implementing Coordinated Exception Handling for Distributed Object-Oriented Systems in AspectJ")) {
				publicationConference = next;
			}
			else if (next.getTitle().equals("Exception handling in the development of dependable component-based systems")) {
				publicationPeriodic = next;
			}
			
			if (publicationConference != null && publicationPeriodic != null) {
				break;
			}
		}
		
		typeConference = publicationConference.getPublicationType();
		typePeriodic = publicationPeriodic.getPublicationType();
	}
	
	/*
	 * When extract curriculo
	 * */
	
	@Test
	public void whenExtractXMLThenCurriculoShouldNotBeNull() {
		assertNotNull(curriculo);
	}
	
	@Test
	public void whenExtractXMLThenCurriculoIdShouldBeCorrect() {
		assertEquals(Long.valueOf("7310046838140771"), curriculo.getId());
	}
	
	@Test
	public void whenExtractXMLThenCurriculoNameShouldBeCorrect() {
		assertEquals("Fernando José Castor de Lima Filho", curriculo.getName());
	}
	
	@Test
	public void whenExtractXMLThenCurriculoLastUpdateShouldBeCorrect() {
		try {
			SimpleDateFormat sdf1= new SimpleDateFormat("dd/MM/yyyy");
			Date dataUsuario = sdf1.parse("25/10/2015");
			assertEquals(dataUsuario, curriculo.getLastUpdate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void whenExtractXMLThenCurriculoPublicationShouldNotBeEmpty() {
		assertFalse(curriculo.getPublications().isEmpty());
	}
	
	@Test
	public void whenExtractXMLThenCurriculoPublicationShouldHaveCorrectSize() {
		assertEquals(71 + 17, curriculo.getPublications().size());
	}
	
	@Test
	public void whenExtractXMLThenConcludedCurriculoOrientationShouldBeCorrect() {
		assertEquals(42, curriculo.getCountConcludedOrientations());
	}
	
	@Test
	public void whenExtractXMLThenOnGoingCurriculoOrientationShouldBeCorrect() {
		assertEquals(9, curriculo.getCountOnGoingOrientations());
	}
	
	/*
	 * Check publications
	 * */
	
	@Test
	public void whenExtractXMLThenCurriculoPublicationShouldHaveCorrectTitle() {
		assertEquals("Implementing Coordinated Exception Handling for Distributed Object-Oriented Systems in AspectJ", publicationConference.getTitle());
		assertEquals("Exception handling in the development of dependable component-based systems", publicationPeriodic.getTitle());
	}
	
	@Test
	public void whenExtractXMLThenCurriculoPublicationShouldHaveCorrectYear() {
		assertEquals(2004, publicationConference.getYear());
		assertEquals(2005, publicationPeriodic.getYear());
	}
	
	@Test
	public void whenExtractXMLThenCurriculoPublicationShouldNullQualis() {
		assertNull(publicationConference.getQualis());
		assertNull(publicationPeriodic.getQualis());
	}
	
	/*
	 * Check publication type
	 * */
	
	@Test
	public void whenExtractXMLThenCurriculoPublicationTypeShouldHaveCorrectName() {
		assertEquals("VIII Simpósio Brasileiro de Linguagens de Programação", typeConference.getName());
		assertEquals("Software, Practice & Experience (Print)", typePeriodic.getName());
	}
	
	@Test
	public void whenExtractXMLThenCurriculoPublicationTypeShouldHaveCorrectType() {
		assertEquals(EnumPublicationLocalType.CONFERENCE, typeConference.getType());
		assertEquals(EnumPublicationLocalType.PERIODIC, typePeriodic.getType());
	}
	
	/*
	 * When extracting invalid file
	 * */
	
	@Test(expected=InvalidPatternFileException.class)
	public void whenExtractInvalidXML() throws InvalidPatternFileException, FileNotFoundException {
		URL url = QualisConferenceExtractorTest.class.getResource("/file_invalid.xml");
		File curriculoFile = new File(url.getFile());
		
		XMLExtractor extractor = new XMLExtractor();
		extractor.lattesExtractor(new FileInputStream(curriculoFile));
	}
	
	@Test(expected=InvalidPatternFileException.class)
	public void whenExtractUnformattedXML() throws InvalidPatternFileException, FileNotFoundException {
		URL url = QualisConferenceExtractorTest.class.getResource("/file.xml");
		File curriculoFile = new File(url.getFile());
		
		XMLExtractor extractor = new XMLExtractor();
		extractor.lattesExtractor(new FileInputStream(curriculoFile));
	}
}
