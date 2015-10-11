package com.brianc.dom;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ElementData {
	public final String tagName;
	Map<String, String> attributes;

	public ElementData(final String tagName, Map<String, String> attributes) {
		this.tagName = tagName;
		this.attributes = attributes;
	}

	public Optional<String> id() {
		return Optional.ofNullable(attributes.get("id"));
	}

	public Set<String> classes() {
		if (!attributes.containsKey("class")) {
			return new HashSet<>();
		}

		return new HashSet<>(Arrays.asList(attributes.get("class").split(" ")));
	}
}
