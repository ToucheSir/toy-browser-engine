package com.brianc.css

import java.util.ArrayList
import java.util.Optional

class SimpleSelector : Selector {
    var tagName: Optional<String>
        internal set
    var id: Optional<String>
        internal set
    var classes: MutableList<String>
        internal set

    init {
        tagName = Optional.empty<String>()
        id = Optional.empty<String>()
        classes = ArrayList<String>()
    }

    override fun specificity(): Specificity {
        val a = id.orElse("").length
        val b = classes.size
        val c = tagName.orElse("").length

        return Specificity(a, b, c)
    }
}
