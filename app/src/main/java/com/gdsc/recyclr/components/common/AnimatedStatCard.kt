package com.gdsc.recyclr.components.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Animated Stat Card with count-up animation
 * Perfect for displaying stats like points, CO2 saved, trees planted
 */
@Composable
fun AnimatedStatCard(
    icon: ImageVector,
    value: Float,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    suffix: String = "",
    decimals: Int = 0
) {
    var animatedValue by remember { mutableStateOf(0f) }
    
    // Animate the value when it changes
    LaunchedEffect(value) {
        val startValue = animatedValue
        val duration = 1000L
        val steps = 60
        val increment = (value - startValue) / steps
        
        repeat(steps) { step ->
            delay(duration / steps)
            animatedValue = startValue + (increment * (step + 1))
        }
        animatedValue = value // Ensure final value is exact
    }
    
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with subtle scale animation
            val iconScale by animateFloatAsState(
                targetValue = if (animatedValue > 0) 1f else 0.8f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Animated value
            Text(
                text = if (decimals > 0) {
                    String.format("%.${decimals}f%s", animatedValue, suffix)
                } else {
                    "${animatedValue.toInt()}$suffix"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = color
            )
            
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Integer version for whole numbers
 */
@Composable
fun AnimatedStatCard(
    icon: ImageVector,
    value: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    suffix: String = ""
) {
    AnimatedStatCard(
        icon = icon,
        value = value.toFloat(),
        label = label,
        color = color,
        modifier = modifier,
        suffix = suffix,
        decimals = 0
    )
}
