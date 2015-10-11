package com.brianc.css;

import java.util.List;

public class Rule {
	public List<Selector> getSelectors() {
		return selectors;
	}

	public List<Declaration> getDeclarations() {
		return declarations;
	}

	List<Selector> selectors;
	List<Declaration> declarations;

	public Rule(List<Selector> selectors, List<Declaration> declarations) {
		this.selectors = selectors;
		this.declarations = declarations;
	}
}
