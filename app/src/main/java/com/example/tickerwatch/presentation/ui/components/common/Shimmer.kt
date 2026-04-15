package com.example.tickerwatch.presentation.ui.components.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private const val GREY_COLOR = 0xFFF2F2F7
private const val LIGHT_GREY_COLOR = 0xFFE5E5EA

// The flash only moves horizontally, so its vertical position is always fixed at the top edge.
private const val SHIMMER_GRADIENT_Y = 0f

// Width of the bright "flash" band that sweeps across the skeleton, in pixels.
private const val SHIMMER_FLASH_WIDTH = 300f

// How far the flash travels before the animation resets (roughly one screen width), in pixels.
const val SHIMMER_TRAVEL_DISTANCE = 1000f

// How long one full left-to-right sweep takes, in milliseconds.
const val SHIMMER_DURATION_MS = 1200

private val shimmerColors = listOf(
    Color(GREY_COLOR),
    Color(LIGHT_GREY_COLOR),
    Color(GREY_COLOR),
)


/**
 * Creates and remembers the animation state that drives the shimmer sweep.
 * Call this once at the top of any composable that needs shimmer, then pass
 * the returned value into [shimmer] on each placeholder shape.
 */
@Composable
fun rememberShimmerTranslateAnim(): State<Float> =
    rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f,                     // flash starts off the left edge
        targetValue = SHIMMER_TRAVEL_DISTANCE, // flash ends off the right edge (~screen width in px)
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart    // snap back to start instead of reversing direction
        ),
        label = "shimmer_translate"
    )

/**
 * Makes any composable look like a shimmer skeleton placeholder.
 *
 * @param translateAnim A number that grows over time, produced by [rememberShimmerTranslateAnim].
 */
fun Modifier.shimmer(translateAnim: State<Float>, listColors: List<Color> = shimmerColors): Modifier = drawBehind {
    drawRect(
        brush = Brush.linearGradient(
            colors = listColors,
            start = Offset(translateAnim.value - SHIMMER_FLASH_WIDTH, SHIMMER_GRADIENT_Y), // left edge of the flash
            end   = Offset(translateAnim.value,                       SHIMMER_GRADIENT_Y)  // right edge of the flash
        )
    )
}
