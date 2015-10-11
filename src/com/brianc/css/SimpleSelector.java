package com.brianc.css;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleSelector implements Selector {
	Optional<String> tagName;
	Optional<String> id;
	List<String> classes;

	public SimpleSelector() {
		tagName = Optional.empty();
		id = Optional.empty();
		classes = new ArrayList<>();
	}

	public Optional<String> getTagName() {
		return tagName;
	}

	public Optional<String> getId() {
		return id;
	}

	public List<String> getClasses() {
		return classes;
	}

	@Override
	public Specificity specificity() {
		int a = id.orElse("").length();
		int b = classes.size();
		int c = tagName.orElse("").length();

		return new Specificity(a, b, c);
	}
}
