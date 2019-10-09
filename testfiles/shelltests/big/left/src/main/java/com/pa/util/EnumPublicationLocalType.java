package com.pa.util;

public enum EnumPublicationLocalType {
	PERIODIC("Periódico"),
	CONFERENCE("Conferência");
	
	private int counter = 0;
	private String name;
	
	private EnumPublicationLocalType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
