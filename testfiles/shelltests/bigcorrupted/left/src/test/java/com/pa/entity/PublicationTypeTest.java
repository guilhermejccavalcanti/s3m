package com.pa.entity;

import static org.junit.Assert.assertNotNull;
import com.pa.entity.PublicationType;
import com.pa.util.EnumPublicationLocalType;

import org.junit.Before;
import org.junit.Test;

public class PublicationTypeTest {
	
	PublicationType pType;
	
	@Before
	public void createPublicationType() {
		pType = new PublicationType("name", EnumPublicationLocalType.CONFERENCE);
	}
	
	/*
	 * When create PublicationType
	 * */
	
	@Test
	public void whenCreatePublicationTypeThenTitleShouldNotBeNull() {
		assertNotNull(pType.getName());
	}

	@Test
	public void whenCreatePublicationTypeThenTypeShouldNotBeNull() {
		assertNotNull(pType.getType());
	}
}
