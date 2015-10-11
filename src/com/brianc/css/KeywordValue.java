package com.brianc.css;

public class KeywordValue implements Value {
	String keyword;

	public KeywordValue(String keyword) {
		this.keyword = keyword;
	}
	
	public Value clone() {
		return new KeywordValue(keyword);
	}
	
	public String getValue() {
		return keyword;
	}
	
	@Override
	public String toString() {
		return "KeywordValue(" + keyword + ")";
	}
}
