package com.brianc.css;

import java.util.Comparator;

public class Specificity implements Comparable<Specificity> {
	int a;
	int b;
	int c;
	
	private static Comparator<Specificity> cmp;
	static {
		// HACK to placate Java's type inference
		cmp = Comparator.comparingInt(s -> s.a);
		cmp = cmp.thenComparingInt(s -> s.b).thenComparingInt(s -> s.c);
	}

	public Specificity(int a, int b, int c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public int compareTo(Specificity other) {
		return cmp.compare(this, other);
	}
}
