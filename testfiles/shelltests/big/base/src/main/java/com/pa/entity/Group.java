package com.pa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="grupo")
public class Group {
	@Id 
	@GeneratedValue
	private Long id;
	
	@Column(unique=true, nullable = false)
	private String name;
	
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	private List<Curriculo> curriculos;
	
	public Group() {}
	
	public Group(String name) {
		this.name = name;
		this.curriculos = new ArrayList<Curriculo>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Curriculo> getCurriculos() {
		return curriculos;
	}

	public void setCurriculos(List<Curriculo> curriculos) {
		this.curriculos = curriculos;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
