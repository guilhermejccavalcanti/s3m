package com.pa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.pa.util.EnumPublicationLocalType;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"name","type"})})
public class PublicationType {
	@Id @GeneratedValue
	private Long identifier;
	
	private String name;
	
	private EnumPublicationLocalType type;
	
	public PublicationType() {}
	
	public PublicationType(String name, EnumPublicationLocalType localType) {
		this.name = name;
		this.type = localType;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public EnumPublicationLocalType getType() {
		return type;
	}
	
	public void setType(EnumPublicationLocalType type) {
		this.type = type;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long id) {
		this.identifier = id;
	}
	
}
