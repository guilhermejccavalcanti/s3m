package com.pa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.pa.util.EnumQualisClassification;

@Entity
public class Qualis {
	@Id @GeneratedValue
	private Long id;
	private String name;
	private EnumQualisClassification classification;
	
	public Qualis(String title, String classification) {
		this.name = title;
		
		if (classification != null) {
			try {
				this.classification = EnumQualisClassification.valueOf(classification);
			}
			catch (IllegalArgumentException e) {
				this.classification = EnumQualisClassification.NONE;
			}
		}
		else {
			this.classification = EnumQualisClassification.NONE;
		}
	}
	
	public Qualis() {
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public EnumQualisClassification getClassification() {
		return classification;
	}
	
	public void setClassification(EnumQualisClassification classification) {
		this.classification = classification;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
