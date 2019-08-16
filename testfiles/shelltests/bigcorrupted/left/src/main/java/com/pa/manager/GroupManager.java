package com.pa.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;

public class GroupManager {
	
	public Group createGroups(String groupName, List<Group> groups) {
		Set<Curriculo> curriculos = new HashSet<Curriculo>(); 
		
		// Avoid duplicate curriculos inside same group
		for (Group group : groups) {
			curriculos.addAll(group.getCurriculos());
		}
		
		// Create new group
		Group newGroup = new Group(groupName);
		newGroup.getCurriculos().addAll(curriculos);
		
		DatabaseFacade.getInstance().saveGroup(newGroup);
		
		return newGroup;
	}

	public boolean checkGroupExistence(String groupName) {
		List<Group> groups = DatabaseFacade.getInstance().listAllGroups(new Group(groupName));
		
		return !groups.isEmpty();
	}
}
