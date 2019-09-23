package com.pa.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.Group;

@ManagedBean(name="manageGroupBean")
@ViewScoped
public class ManageGroup {
	private List<Group> groups;
	
	@PostConstruct
	public void init() {
		groups = DatabaseFacade.getInstance().listAllGroups();
	}
	
	public void deleteGroup(Group group) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			DatabaseFacade.getInstance().refreshGroup(group);
			DatabaseFacade.getInstance().deleteGroup(group);

			Group checkGroupDeleted = DatabaseFacade.getInstance().getGroupById(group.getId());

			if(checkGroupDeleted == null) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "O grupo " + group.getName() + " foi deletado com sucesso", null) );
				init();
			}
			else {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível deletar o grupo. Contacte o admnistrador do sistema", null) );
			}
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Não foi possível deletar o grupo. Contacte o admnistrador do sistema e mostre a mensagem: " + e.getMessage(), null) );
		}
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
