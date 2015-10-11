package com.brianc.style;

import com.brianc.css.Rule;
import com.brianc.css.Specificity;

public class MatchedRule {
	Specificity s;
	Rule r;
	
	public MatchedRule(Specificity s, Rule r) {
		this.s = s;
		this.r = r;
	}
}
