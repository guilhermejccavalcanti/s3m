package com.pa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;

import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.QualisData;
import com.pa.exception.InvalidPatternFileException;
import com.pa.extractor.QualisExtractor;
import com.pa.util.EnumPublicationLocalType;
 
@ManagedBean(name="loadQualisBean")
@ViewScoped
public class LoadQualisBean {
     
    private List<String> qualisYears;
    private String qualisYear;
    private String qualisType;    
    private QualisData qualisData;
    private List<QualisData> qualisDatas;
    private List<QualisData> filteredQualisDatas;
    private Set<Integer> years;
    
    @PostConstruct
    public void init() {
    	qualisYears = new ArrayList<String>();
    	qualisYears.add("2017");
    	qualisYears.add("2016");
    	qualisYears.add("2015");
    	qualisYears.add("2014");
    	qualisYears.add("2013");
    	qualisYears.add("2012");
    	qualisYears.add("2011");
    	qualisYears.add("2010");
    	qualisYears.add("2009");
    	qualisYears.add("2008");
    	qualisYears.add("2007");
    	qualisYears.add("2006");
    	qualisYears.add("2005");  
    	    	
    	// Get all QualisData from Database.
		qualisDatas = DatabaseFacade.getInstance().listAllQualisData();
    }

    public void uploadQualisFile(FileUploadEvent event) throws IOException {    	
    	QualisExtractor extractor = new QualisExtractor();
    	FacesContext context = FacesContext.getCurrentInstance();
    	try {
	    	if(qualisType.equals(EnumPublicationLocalType.PERIODIC.toString())){
				qualisData = extractor.publicationExtractor(qualisYear, event.getFile().getInputstream(), event.getFile().getFileName());
	    	}else if(qualisType.equals(EnumPublicationLocalType.CONFERENCE.toString())){
	    		qualisData = extractor.conferenceExtractor(qualisYear, event.getFile().getInputstream(), event.getFile().getFileName());
	    	}	
	    	
	    	// reset interface values.
	    	qualisYear = null;
	    	qualisType = null;
	    	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Arquivo carregado com sucesso!", null) );
    	} catch (InvalidPatternFileException e) {
    		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O arquivo selecionado é inválido.", null) );
    	}
    }

	public List<String> getQualisYears() {
		return qualisYears;
	}

	public void setQualisYears(List<String> qualisYears) {
		this.qualisYears = qualisYears;
	}

	public String getQualisYear() {
		return qualisYear;
	}

	public void setQualisYear(String qualisYear) {
		this.qualisYear = qualisYear;
	}

	public String getQualisType() {
		return qualisType;
	}

	public void setQualisType(String qualisType) {
		this.qualisType = qualisType;
	}

	public QualisData getQualisData() {
		return qualisData;
	}

	public void setQualisData(QualisData qualisData) {
		this.qualisData = qualisData;
	}

	public List<QualisData> getQualisDatas() {		
    	// Get all QualisData from Database.
		qualisDatas = DatabaseFacade.getInstance().listAllQualisData();
		
		return qualisDatas;
	}

	public void setQualisDatas(List<QualisData> qualisDatas) {
		this.qualisDatas = qualisDatas;
	}

	public List<QualisData> getFilteredQualisDatas() {
		return filteredQualisDatas;
	}

	public void setFilteredQualisDatas(List<QualisData> filteredQualisDatas) {
		this.filteredQualisDatas = filteredQualisDatas;
	}

	public Set<Integer> getYears() {
		
		years = new HashSet<Integer>();
		
		for (QualisData qualisData : qualisDatas) {			
			years.add(qualisData.getYear());			
		}
		
		return years;
	}

	public void setYears(Set<Integer> years) {
		this.years = years;
	}
}