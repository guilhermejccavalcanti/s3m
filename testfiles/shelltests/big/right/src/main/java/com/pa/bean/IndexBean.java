package com.pa.bean;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.Book;
import com.pa.entity.Chapter;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.entity.Orientation;
import com.pa.entity.Publication;
import com.pa.entity.Qualis;
import com.pa.entity.TechnicalProduction;
import com.pa.manager.RelatorioManager;

import net.sf.jasperreports.engine.JRException;

@ManagedBean(name="indexBean")
@ViewScoped
public class IndexBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer groupsSize;
	private Integer curriculosSize;
	private Integer qualisSize;
	private Integer publicationsSize;
	private Integer orientationsSize;
	private Integer technicalProductionSize;
	private Integer booksSize;
	private Integer chapterSize;
	private RelatorioManager relatorioManager; 
	private String data1;
	private String data2;

	@PostConstruct
	public void init() {
		relatorioManager = new RelatorioManager();
		List<Group> groups = DatabaseFacade.getInstance().listAllGroups();
		List<Curriculo> curriculum = DatabaseFacade.getInstance().listAllCurriculos();
		List<Qualis> qualis = DatabaseFacade.getInstance().listAllQualis();
		List<TechnicalProduction> technicalProductions = DatabaseFacade.getInstance().listAllTechnicalProductions();
		List<Orientation> orientations = DatabaseFacade.getInstance().listAllOrientations();
		List<Publication> publications = DatabaseFacade.getInstance().listAllPublications();
		List<Book> books = DatabaseFacade.getInstance().listAllBooks();
		List<Chapter> chapters = DatabaseFacade.getInstance().listAllChapters();

		groupsSize = groups.size();
		curriculosSize = curriculum.size();
		qualisSize = qualis.size();
		technicalProductionSize = technicalProductions.size();
		orientationsSize = orientations.size();
		publicationsSize = publications.size();
		booksSize = books.size();
		chapterSize = chapters.size();
		data1 = "";
		data2 = "";
	}
	
	public void relatorioLattes(){
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			relatorioManager.gerarRelatorioLattes(data1, data2);
			System.out.println("Fim...");
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Relatório gerando com sucesso, local:" + System.getProperty("user.home") + "//", null) );
		} catch (JRException | SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public Integer getBooksSize() {
		return booksSize;
	}

	public Integer getChapterSize() {
		return chapterSize;
	}

	public Integer getPublicationsSize() {
		return publicationsSize;
	}

	public Integer getOrientationsSize() {
		return orientationsSize;
	}

	public Integer getTechnicalProductionsSize() {
		return technicalProductionSize;
	}

	public Integer getGroupsSize() {
		return groupsSize;
	}

	public void setGroupsSize(Integer groupsSize) {
		this.groupsSize = groupsSize;
	}

	public Integer getCurriculosSize() {
		return curriculosSize;
	}

	public void setCurriculosSize(Integer curriculosSize) {
		this.curriculosSize = curriculosSize;
	}

	public Integer getQualisSize() {
		return qualisSize;
	}

	public void setQualisSize(Integer qualisSize) {
		this.qualisSize = qualisSize;
	}
	
	
}
