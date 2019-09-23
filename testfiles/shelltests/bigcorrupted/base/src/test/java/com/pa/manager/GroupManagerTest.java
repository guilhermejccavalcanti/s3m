package com.pa.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pa.database.util.HibernateUtil;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.manager.GroupManager;

import org.junit.BeforeClass;
import org.junit.Test;

public class GroupManagerTest {
	GroupManager manager = new GroupManager();
	
	static Group factoryFloor = new Group("Chão de fábrica");
	static Group leader = new Group("Gerente");
	static Group financial = new Group("Financeiro");
	static Group support = new Group("Suporte");
	static Group moto = new Group("Motorola");
	
	@BeforeClass
	static public void createGroups() {
		HibernateUtil.createSessionFactory("hibernateTest.cfg.xml");
		
		Curriculo nielson = new Curriculo("Nielson", new Date());
		nielson.setId((long) 123456);
		Curriculo fernando = new Curriculo("Fernando", new Date());
		fernando.setId((long) 456789);
		Curriculo jessica = new Curriculo("Jéssica", new Date());
		jessica.setId((long) 456123);
		Curriculo tiago = new Curriculo("Tiago", new Date());
		tiago.setId((long) 456123);
		Curriculo juana = new Curriculo("Juana", new Date());
		juana.setId((long) 951236);
		Curriculo marcio = new Curriculo("Márcio", new Date());
		marcio.setId((long) 456896);
		Curriculo alexandre = new Curriculo("Alexandre", new Date());
		alexandre.setId((long) 985623);
		
		factoryFloor.getCurriculos().add(nielson);
		factoryFloor.getCurriculos().add(fernando);
		factoryFloor.getCurriculos().add(jessica);
		
		leader.getCurriculos().add(tiago);
		
		financial.getCurriculos().add(juana);
		
		support.getCurriculos().add(alexandre);
		support.getCurriculos().add(marcio);
		
		moto.getCurriculos().add(alexandre);
		moto.getCurriculos().add(marcio);
		moto.getCurriculos().add(juana);
	}

	/*
	 * When creating groups
	 * */
	
	@Test
	public void whenCreatingGroupThenNewGroupShouldHaveCorrectName() {
		List<Group> newGroup = new ArrayList<Group>();
		newGroup.add(leader);
		newGroup.add(financial);
		
		Group admin = manager.createGroups("Administração", newGroup);
		
		assertEquals("Administração", admin.getName());
	}
	
	@Test
	public void whenCreatingGroupThenNewGroupShouldHaveCorrectCurriculosSize() {
		List<Group> newGroup = new ArrayList<Group>();
		newGroup.add(leader);
		newGroup.add(financial);
		newGroup.add(support);
		newGroup.add(factoryFloor);
		
		Group tvd = manager.createGroups("TVD", newGroup);
		
		assertEquals(7, tvd.getCurriculos().size());
	}
	
	@Test
	public void whenCreatingGroupThenNewGroupShouldHaveCorrectCurriculoSize() {
		List<Group> newGroup = new ArrayList<Group>();
		newGroup.add(support);
		newGroup.add(leader);
		newGroup.add(financial);
		newGroup.add(factoryFloor);
		newGroup.add(moto);
		
		Group tvd = manager.createGroups("tvd", newGroup);
		
		assertEquals(7, tvd.getCurriculos().size());
	}
	
	/*
	 * When verifying group existence
	 * */
	@Test
	public void whenCreateGroupWithSameNameThenReturnShouldBeTrue() {
		assertTrue(manager.checkGroupExistence("TVD"));
	}
	
	@Test
	public void whenCreateGroupWithDiferentNameThenReturnShouldBeFalse() {
		assertFalse(manager.checkGroupExistence("Novo"));
	}
}
