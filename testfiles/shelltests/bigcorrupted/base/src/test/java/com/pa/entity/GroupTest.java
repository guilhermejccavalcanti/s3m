package com.pa.entity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import com.pa.entity.Group;

import org.junit.Before;
import org.junit.Test;

public class GroupTest {
	Group group;
	
	@Before
	public void createGroup() {
		group = new Group("UFPE");
	}
	
	/*
	 * When create Group
	 * */
	
	@Test
	public void whenCreateGroupThenNameShouldNotBeNull() {
		assertNotNull(group.getName());
	}
	
	@Test
	public void whenCreateGroupThenCurriculosShouldBeEmpty() {
		assertNotNull(group.getCurriculos());
		assertTrue(group.getCurriculos().isEmpty());
	}
}
