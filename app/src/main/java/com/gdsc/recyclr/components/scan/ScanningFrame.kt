package com.gdsc.recyclr.components.scan

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

/**
 * Animated scanning frame with corner brackets and moving scan line
 * Creates a professional camera scanning interface
 */
@Composable
fun ScanningFrame(
    modifier: Modifier = Modifier,
    frameColor: Color = Color.White,
    scanLineColor: Color = Color.Green
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    
    // Animate scan line from top to bottom
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )
    
    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Corner brackets
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cornerLength = 40.dp.toPx()
            val strokeWidth = 6.dp.toPx()
            
            // Top-left corner
            drawLine(
                color = frameColor,
                start = Offset(0f, cornerLength),
                end = Offset(0f, 0f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(0f, 0f),
                end = Offset(cornerLength, 0f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            
            // Top-right corner
            drawLine(
                color = frameColor,
                start = Offset(size.width - cornerLength, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(size.width, 0f),
                end = Offset(size.width, cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            
            // Bottom-left corner
            drawLine(
                color = frameColor,
                start = Offset(0f, size.height - cornerLength),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(0f, size.height),
                end = Offset(cornerLength, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            
            // Bottom-right corner
            drawLine(
                color = frameColor,
                start = Offset(size.width - cornerLength, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(size.width, size.height - cornerLength),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        
        // Animated scan line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .offset(y = (280.dp * scanLinePosition) - 140.dp)
                .background(scanLineColor.copy(alpha = 0.8f))
        )
    }
}
