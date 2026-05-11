package com.gdsc.recyclr.components.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaveBand(
    modifier: Modifier = Modifier,
    color: Color,
    height: Dp = 220.dp,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(0f, h * 0.35f)
            cubicTo(w * 0.25f, h * 0.05f, w * 0.75f, h * 0.65f, w, h * 0.35f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(path, color)
    }
}

@Composable
fun CurvedHeaderShape(
    modifier: Modifier = Modifier,
    color: Color,
    height: Dp = 180.dp,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, h * 0.72f)
            cubicTo(w * 0.75f, h, w * 0.25f, h * 0.88f, 0f, h * 0.72f)
            close()
        }
        drawPath(path, color)
    }
}
