package com.brianc.css;

public interface Value {
	Value clone();
	
	default float toPx() {
		return 0;
	}
}
