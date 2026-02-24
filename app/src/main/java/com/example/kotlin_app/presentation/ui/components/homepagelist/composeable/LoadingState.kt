package com.example.kotlin_app.presentation.ui.components.homepagelist.composeable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.runtime.State
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val shimmerColors = listOf(
    Color(0xFFF2F2F7),
    Color(0xFFE5E5EA),
    Color(0xFFF2F2F7),
)

/**
 * Draws the shimmer brush in the draw phase, not the composition phase.
 * Changes to [translateAnim] only invalidate the draw layer — no recomposition.
 */
private fun Modifier.shimmer(translateAnim: State<Float>): Modifier = drawBehind {
    drawRect(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim.value - 300f, 0f),
            end = Offset(translateAnim.value, 0f),
        )
    )
}

@Composable
fun LoadingState(message: String = "") {
    // Obtain State<Float> without reading .value here — no composition invalidation on each frame.
    val translateAnim = rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(10) {
            SkeletonStockItem(translateAnim)
        }
    }
}

@Composable
private fun SkeletonStockItem(translateAnim: State<Float>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Left — icon + name/symbol/chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .shimmer(translateAnim)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer(translateAnim)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer(translateAnim)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer(translateAnim)
                    )
                }
            }

            // Middle — sparkline
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(translateAnim)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Right — price + badge
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer(translateAnim)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmer(translateAnim)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = Color(0xFFEEEEEE)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingStatePreview() {
    LoadingState()
}