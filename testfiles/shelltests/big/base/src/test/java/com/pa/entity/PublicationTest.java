package com.pa.entity;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.util.EnumPublicationLocalType;

import org.junit.Before;
import org.junit.Test;

public class PublicationTest {
	
	Publication publication;
	
	@Before
	public void createPublicationType() {
		PublicationType type = new PublicationType("Name", EnumPublicationLocalType.CONFERENCE);
		publication = new Publication("Title", 2013, type, null);
	}
	
	/*
	 * When create Publication
	 * */
	
	@Test
	public void whenCreatePublicationThenTitleShouldNotBeNull() {
		assertNotNull(publication.getTitle());
	}
	
	@Test
	public void whenCreatePublicationThenYearShouldNotBeZero() {
		assertNotEquals(0, publication.getYear());
	}
	
	@Test
	public void whenCreatePublicationThenPublicationTypeSHouldNotBeNull() {
		assertNotNull(publication.getPublicationType());
	}
	
	@Test
	public void whenCreatePublicationThenQualisShouldBeNull() {
		assertNull(publication.getQualis());
	}
}
