package com.pa.util;

public enum EnumPublicationLocalType {
	PERIODIC("Periódico"),
	CONFERENCE("Conferência");
	
	private int number = 23;
	
	private EnumPublicationLocalType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNameNumber() {
		return name + this.number ;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
