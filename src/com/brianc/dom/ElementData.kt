package com.brianc.dom

import java.util.HashSet
import java.util.Optional

class ElementData(val tagName: String, internal var attributes: Map<String, String>) {

    fun id(): Optional<String> {
        return Optional.ofNullable<String>(attributes["id"])
    }

    fun classes(): Set<String> {
        if (!attributes.containsKey("class")) {
            return HashSet()
        }
        return (attributes["class"] ?: "").split(" ").dropLastWhile(String::isEmpty).toHashSet()
    }
}
