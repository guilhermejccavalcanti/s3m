package com.pa.comparator;

import java.util.ArrayList;
import java.util.List;

public class ComparationVO {

	private String name;
	private List<String> values = new ArrayList<String>();
	
	public ComparationVO(String name, String... value) {
		this.name = name;
		
		for (String string : value) {
			values.add(string);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> value) {
		this.values = value;
	}
	
	
}
