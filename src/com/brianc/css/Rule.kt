package com.brianc.css

class Rule(selectors: List<Selector>, declarations: List<Declaration>) {

    var selectors: List<Selector>
        internal set
    var declarations: List<Declaration>
        internal set

    init {
        this.selectors = selectors
        this.declarations = declarations
    }
}
