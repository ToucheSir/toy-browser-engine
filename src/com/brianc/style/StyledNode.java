package com.brianc.style;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brianc.css.Declaration;
import com.brianc.css.KeywordValue;
import com.brianc.css.Rule;
import com.brianc.css.Selector;
import com.brianc.css.SimpleSelector;
import com.brianc.css.StyleSheet;
import com.brianc.css.Value;
import com.brianc.dom.ElementData;
import com.brianc.dom.ElementNode;
import com.brianc.dom.Node;
import com.brianc.dom.NodeType;

public class StyledNode {
	private Node node;
	public Node getNode() {
		return node;
	}

	Map<String, Value> specifiedValues;
	public final List<StyledNode> children;

	public StyledNode(Node node, Map<String, Value> specifiedValues, final List<StyledNode> children) {
		super();
		this.node = node;
		this.specifiedValues = specifiedValues;
		this.children = children;
	}

	public Optional<Value> value(String name) {
		return Optional.ofNullable(specifiedValues.get(name)).map(val -> val.clone());
	}

	public Value lookup(String name, String fallbackName, Value defaultVal) {
		return value(name).orElseGet(() -> value(fallbackName).orElse(defaultVal.clone()));
	}

	public Display display() {
		return value("display")
				.map(val -> (KeywordValue) val)
				.map(KeywordValue::getValue)
				.map(Display::find)
				.orElseGet(() -> Display.INLINE);
	}

	private static boolean matchesSimpleSelector(ElementData elem, SimpleSelector selector) {
		if (selector.getTagName().filter(name -> !elem.tagName.equals(name)).isPresent()) {
			return false;
		}
		
		if (selector.getId().filter(id -> !elem.id().equals(Optional.ofNullable(id))).isPresent()) {
			return false;
		}

		if (selector.getClasses().stream().anyMatch(clazz -> !elem.classes().contains(clazz))) {
			return false;
		}
		
		return true;
	}

	private static Optional<MatchedRule> matchRule(ElementData elem, Rule rule) {
		return rule.getSelectors().stream()
				.filter(s -> matches(elem, s))
				.findFirst()
				.map(s -> new MatchedRule(s.specificity(), rule));
	}

	private static List<MatchedRule> matchingRules(ElementData elem, StyleSheet stylesheet) {
		return stylesheet.getRules().stream()
				.map(rule -> matchRule(elem, rule))
				.filter(o -> o.isPresent())
				.map(o -> o.get())
				.collect(Collectors.toList());
	}

	private static boolean matches(ElementData elem, Selector selector) {
		return matchesSimpleSelector(elem, (SimpleSelector) selector);
	}

	private static Map<String, Value> specifiedValues(ElementData elem, StyleSheet stylesheet) {
		Map<String, Value> values = new HashMap<>();
		List<MatchedRule> rules = matchingRules(elem, stylesheet);

		rules.sort((a, b) -> a.s.compareTo(b.s));
		for (MatchedRule m : rules) {
			for (Declaration declaration : m.r.getDeclarations()) {
				values.put(declaration.name, declaration.value.clone());
			}
		}

		return values;
	}

	public static StyledNode styleTree(Node root, StyleSheet stylesheet) {
		Map<String, Value> specifiedValues = root.getType() == NodeType.ELEMENT ? specifiedValues(
				((ElementNode) root).getData(), stylesheet) : new HashMap<>();

		return new StyledNode(root, specifiedValues, root.getChildren().stream()
				.map(child -> styleTree(child, stylesheet)).collect(Collectors.toList()));
	}
}
