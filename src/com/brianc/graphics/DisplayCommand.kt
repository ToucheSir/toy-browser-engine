package com.brianc.graphics

import com.brianc.css.Color
import com.brianc.layout.InlineBox.LineBox
import com.brianc.layout.Rect

sealed class DisplayCommand

data class SolidColor(val color: Color, val rect: Rect): DisplayCommand()
data class RenderText(val text: String, val lines: List<LineBox>, val rect: Rect, val color: Color): DisplayCommand()
