package com.brianc.style

import java.util.HashMap
import java.util.Optional
import java.util.stream.Collectors

import com.brianc.css.Declaration
import com.brianc.css.KeywordValue
import com.brianc.css.Rule
import com.brianc.css.Selector
import com.brianc.css.SimpleSelector
import com.brianc.css.StyleSheet
import com.brianc.css.Value
import com.brianc.dom.ElementData
import com.brianc.dom.ElementNode
import com.brianc.dom.Node
import com.brianc.dom.NodeType

class StyledNode(val node: Node, internal var specifiedValues:

Map<String, Value>, val children: List<StyledNode>) {

    fun value(name: String): Optional<Value> {
        return Optional.ofNullable<Value>(specifiedValues[name]).map({ `val` -> `val`.clone() })
    }

    fun lookup(name: String, fallbackName: String, defaultVal: Value): Value {
        return value(name).orElseGet { value(fallbackName).orElse(defaultVal.clone()) }
    }

    fun display(): Display {
        return value("display").map({ `val` -> `val` as KeywordValue }).map({ it.value }).map({ Display.find(it) }).orElseGet { Display.INLINE }
    }

    companion object {

        private fun matchesSimpleSelector(elem: ElementData, selector: SimpleSelector): Boolean {
            if (selector.tagName.filter { name -> elem.tagName != name }.isPresent) {
                return false
            }

            if (selector.id.filter { id -> elem.id() != Optional.ofNullable(id) }.isPresent) {
                return false
            }

            if (selector.classes.any({ clazz -> !elem.classes().contains(clazz) })) {
                return false
            }

            return true
        }

        private fun matchRule(elem: ElementData, rule: Rule): Optional<MatchedRule> {
            return Optional.ofNullable(rule.selectors.filter({ s -> matches(elem, s) }).firstOrNull()).map({ s -> MatchedRule(s!!.specificity(), rule) })
        }

        private fun matchingRules(elem: ElementData, stylesheet: StyleSheet): List<MatchedRule> {
            return stylesheet.rules.map({ rule -> matchRule(elem, rule) }).filter({ o -> o.isPresent() }).map({ o -> o.get() })
        }

        private fun matches(elem: ElementData, selector: Selector): Boolean {
            return matchesSimpleSelector(elem, selector as SimpleSelector)
        }

        private fun specifiedValues(elem: ElementData, stylesheet: StyleSheet): Map<String, Value> {
            val values = HashMap<String, Value>()
            val rules = matchingRules(elem, stylesheet)

            val sortedRules = rules.sortedBy { it.s }
            for (m in sortedRules) {
                for (declaration in m.r.declarations) {
                    values.put(declaration.name, declaration.value.clone())
                }
            }

            return values
        }

        fun styleTree(root: Node, stylesheet: StyleSheet): StyledNode {
            val specifiedValues = if (root.type == NodeType.ELEMENT)
                specifiedValues(
                        (root as ElementNode).data, stylesheet)
            else
                HashMap<String, Value>()

            return StyledNode(root, specifiedValues, root.children.map({ child -> styleTree(child, stylesheet) }))
        }
    }
}
