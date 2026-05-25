package com.gdsc.recyclr.components.results

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * Celebration confetti animation for success screens
 * Creates falling confetti particles with random colors
 */
@Composable
fun CelebrationConfetti(
    modifier: Modifier = Modifier,
    particleCount: Int = 30
) {
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    
    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * -0.2f,
                color = listOf(
                    Color(0xFF4CAF50), // Green
                    Color(0xFFFFA726), // Orange
                    Color(0xFF42A5F5), // Blue
                    Color(0xFFEF5350), // Red
                    Color(0xFFAB47BC), // Purple
                    Color(0xFFFFEE58)  // Yellow
                ).random(),
                size = (4..12).random().dp,
                speed = (2500..4000).random()
            )
        }
    }
    
    // Animate all particles outside Canvas
    val animatedYValues = particles.map { particle ->
        val animatedY by infiniteTransition.animateFloat(
            initialValue = particle.startY,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(particle.speed, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_${particle.hashCode()}"
        )
        animatedY
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEachIndexed { index, particle ->
            val animatedY = animatedYValues[index]
            
            // Add slight horizontal drift
            val drift = kotlin.math.sin(animatedY * 3.14f * 2) * 20f
            
            drawCircle(
                color = particle.color,
                radius = particle.size.toPx(),
                center = Offset(
                    x = size.width * particle.x + drift,
                    y = size.height * animatedY
                ),
                alpha = if (animatedY > 1f) 0f else 0.8f
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val color: Color,
    val size: Dp,
    val speed: Int
)
