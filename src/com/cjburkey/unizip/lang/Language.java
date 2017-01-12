package com.cjburkey.unizip.lang;

import java.util.Map;

public class Language {
	
	private String[] name;
	private Map<String, String> keys;
	
	public Language(String name, String fname, Map<String, String> keys) {
		this.name = new String[] { name, fname };
		this.keys = keys;
	}
	
	public String getName() {
		return this.name[0];
	}
	
	public String getCodeName() {
		return this.name[1].substring(0, this.name[1].length() - 5);
	}
	
	public String toString() {
		return this.getName();
	}
	
	public Map<String, String> getKeys() {
		return this.keys;
	}
	
}