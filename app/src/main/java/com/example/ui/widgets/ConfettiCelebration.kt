package com.example.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import java.util.Random

class StableConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val size: Float,
    val color: Color,
    val shapeType: Int, // 0: Circle, 1: Square, 2: Triangle
    var rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiCelebration(
    trigger: Boolean,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    val particles = remember { mutableStateListOf<StableConfettiParticle>() }
    val random = remember { Random() }

    val colors = remember {
        listOf(
            Color(0xFF00F0FF), // RetroCyan
            Color(0xFF10B981), // EmeraldGreen
            Color(0xFFFBBF24), // QuestAmber
            Color(0xFF6366F1), // ElectricIndigo
            Color(0xFF3B82F6), // XPBlue
            Color(0xFFFF4E4E), // Crimson Danger
            Color(0xFFFFD700)  // Gold Star Medallion
        )
    }

    LaunchedEffect(key1 = trigger) {
        val count = 110
        particles.clear()
        
        // Spawn standard symmetric celebration cannons from the lower corners
        for (i in 0 until count) {
            val angle = random.nextFloat() * (Math.PI / 3) + (Math.PI / 12) // Angle between 15 and 75 degrees
            val speed = 10f + random.nextFloat() * 16f
            
            val isLeftCannon = i % 2 == 0
            val startX = if (isLeftCannon) screenWidth * 0.05f else screenWidth * 0.95f
            val startY = screenHeight * 0.85f

            // Adjust angle orientation (left cannon fires right-up, right cannon fires left-up)
            val finalAngle = if (isLeftCannon) -angle else -(Math.PI - angle)
            
            val vx = (Math.cos(finalAngle) * speed).toFloat()
            val vy = (Math.sin(finalAngle) * speed).toFloat()

            particles.add(
                StableConfettiParticle(
                    x = startX,
                    y = startY,
                    vx = vx,
                    vy = vy,
                    size = 12f + random.nextFloat() * 22f,
                    color = colors[random.nextInt(colors.size)],
                    shapeType = random.nextInt(3),
                    rotation = random.nextFloat() * 360f,
                    rotationSpeed = -6f + random.nextFloat() * 12f
                )
            )
        }

        var ticks = 0
        while (isActive && particles.isNotEmpty() && ticks < 180) {
            withFrameMillis {
                for (i in particles.indices.reversed()) {
                    val p = particles[i]
                    p.x += p.vx
                    p.y += p.vy
                    
                    // Apply low gravity pull
                    p.vy += 0.38f
                    // Apply atmospheric drag friction
                    p.vx *= 0.982f
                    p.vy *= 0.99f
                    p.rotation += p.rotationSpeed

                    // Dismiss went-past boundaries
                    if (p.y > screenHeight + 60f || p.x < -60f || p.x > screenWidth + 60f) {
                        particles.removeAt(i)
                    }
                }
            }
            ticks++
        }
        onAnimationEnd()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                when (p.shapeType) {
                    0 -> { // Circle particle
                        drawCircle(
                            color = p.color,
                            radius = p.size / 2f,
                            center = Offset(p.x, p.y)
                        )
                    }
                    1 -> { // Rectangular stripe
                        drawRect(
                            color = p.color,
                            topLeft = Offset(p.x - p.size, p.y - p.size / 2.5f),
                            size = Size(p.size * 2f, p.size / 1.2f)
                        )
                    }
                    2 -> { // Triangular streamer
                        val triPath = Path().apply {
                            moveTo(p.x, p.y - p.size)
                            lineTo(p.x - p.size, p.y + p.size)
                            lineTo(p.x + p.size, p.y + p.size)
                            close()
                        }
                        drawPath(path = triPath, color = p.color)
                    }
                }
            }
        }
    }
}
