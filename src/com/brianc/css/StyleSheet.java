package com.brianc.css;

import java.util.List;

public class StyleSheet {
	private List<Rule> rules;

	
	public StyleSheet(List<Rule> rules) {
		this.rules = rules;
	}

	public List<Rule> getRules() {
		return rules;
	}
}
