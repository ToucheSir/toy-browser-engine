package com.brianc.style

enum class Display {
    INLINE, BLOCK, NONE;


    companion object {

        fun find(name: String): Display {
            when (name) {
                "block" -> return BLOCK
                "none" -> return NONE
                else -> return INLINE
            }
        }
    }
}
