package com.pa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.pa.util.EnumPublicationLocalType;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"year","type"})})
public class QualisData {
	@Id @GeneratedValue
	private Long id;
	
	private String fileName;
	private EnumPublicationLocalType type;
	private Integer year;
	
	@OneToMany(cascade= CascadeType.ALL)
	private List<Qualis> qualis;
	
	public QualisData() {}
	
	public QualisData(String file, EnumPublicationLocalType type, int year) {
		this.fileName = file;
		this.type = type;
		this.year = year;
		
		this.qualis = new ArrayList<Qualis>();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public EnumPublicationLocalType getType() {
		return type;
	}
	
	public void setType(EnumPublicationLocalType type) {
		this.type = type;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<Qualis> getQualis() {
		return qualis;
	}
	
	public void setQualis(List<Qualis> qualis) {
		this.qualis = qualis;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
