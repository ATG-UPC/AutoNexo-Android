package com.atg.autonexo.core.ui.components

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp

class BottomArcShape(private val curveDp: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): androidx.compose.ui.graphics.Outline {
        val curve = with(density) { curveDp.toPx() }
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(0f, 0f)          // esquina sup. izq
            lineTo(w, 0f)           // sup. der
            lineTo(w, h - curve)    // baja recto
            // curva cóncava al centro y sube
            quadraticTo(w / 2f, h + curve, 0f, h - curve)
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}
