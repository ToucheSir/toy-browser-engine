package com.brianc.graphics

import java.awt.Graphics2D

interface DisplayCommand {
    fun paint(g: Graphics2D)
}
