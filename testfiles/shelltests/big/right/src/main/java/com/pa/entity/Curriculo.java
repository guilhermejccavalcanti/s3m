package com.pa.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Curriculo {
	@Id
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private int countConcludedOrientations;
	
	@Column
	private int countOnGoingOrientations;
	
	@OneToMany(cascade=CascadeType.ALL)
	private Set<Publication> publications;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Book> books;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Chapter> chapters;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Orientation> orientations;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<TechnicalProduction> technicalProduction;
	
	@Column
	private Date lastUpdate;
	
	public Curriculo() {}
	
	public Curriculo(String name, Date lastUpdate) {
		this.name = name;
		this.lastUpdate = lastUpdate;
		this.publications = new HashSet<Publication>();
	}
	
	
	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	public Integer getCountPublications(){
		return this.publications.size();
	}
	
	public List<TechnicalProduction> getTechnicalProduction() {
		return technicalProduction;
	}

	public void setTechnicalProduction(List<TechnicalProduction> technicalProduction) {
		this.technicalProduction = technicalProduction;
	}

	
	public List<Orientation> getOrientations() {
		return orientations;
	}

	public void setOrientations(List<Orientation> orientations) {
		this.orientations = orientations;
	}

	public void setTechinicalProduction(List<TechnicalProduction> techinicalProduction) {
		this.technicalProduction = techinicalProduction;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public int getCountConcludedOrientations() {
		return countConcludedOrientations;
	}

	public void setCountConcludedOrientations(int countConcludedOrientations) {
		this.countConcludedOrientations = countConcludedOrientations;
	}

	public int getCountOnGoingOrientations() {
		return countOnGoingOrientations;
	}

	public void setCountOnGoingOrientations(int countOnGoingOrientations) {
		this.countOnGoingOrientations = countOnGoingOrientations;
	}

	public Set<Publication> getPublications() {
		return publications;
	}
	
	public void setPublications(Set<Publication> publications) {
		this.publications = publications;
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
