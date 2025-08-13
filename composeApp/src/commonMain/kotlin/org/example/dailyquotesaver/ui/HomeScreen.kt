package org.example.dailyquotesaver.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.dailyquotesaver.Quote
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

// ---------- Public entry point ----------

@Composable
fun HomeScreen(
    quote: Quote?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(modifier = Modifier.fillMaxSize())
        HomeContent(
            quote = quote,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

// ---------- UI content ----------

@Composable
private fun HomeContent(
    quote: Quote?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            SimpleQuoteCard(quote = quote, modifier = Modifier.fillMaxWidth())
            // Spacer(modifier = Modifier.height(24.dp)) // Removed
            // ViewAllQuotesButton(onClick = onNavigateToQuoteScreen) // Removed
        }
    }
}

@Composable
private fun SimpleQuoteCard(
    quote: Quote?,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quote of the Day",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (quote == null) {
                Text("No quotes available. Add some to get started!")
            } else {
                Text(
                    text = "“${quote.text}”",
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                quote.author?.let {
                    Text(
                        text = "- $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// @Composable // Removed
// private fun ViewAllQuotesButton(onClick: () -> Unit) { // Removed
//     Button( // Removed
//         onClick = onClick, // Removed
//         modifier = Modifier // Removed
//             .fillMaxWidth() // Removed
//             .height(48.dp), // Removed
//         shape = RoundedCornerShape(24.dp) // Removed
//     ) { // Removed
//         Text("View All Quotes") // Removed
//     } // Removed
// } // Removed

// ---------- Animated background (sway + haze) ----------

@Composable
private fun AnimatedBackground(modifier: Modifier = Modifier) {
    // Single transition drives all animations
    val transition = rememberInfiniteTransition(label = "bgTransition")

    // Breathing stop position
    val blueFraction by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blueFraction"
    )

    // Palette phase
    val palette = listOf(
        Color(0xFFB3E5FC), // blue
        Color(0xFFF8BBD0), // pink
        Color(0xFFC8E6C9), // green
        Color(0xFFFFE0B2)  // orange
    )
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = palette.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "palettePhase"
    )

    // Gentle sway angle
    val ripplePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripplePhase"
    )
    val angleDeg = 90f + 10f * sin(ripplePhase) // ~85° .. ~95°

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val wPx = with(density) { maxWidth.toPx() }
        val hPx = with(density) { maxHeight.toPx() }

        // Palette crossfade
        val base = floor(phase).toInt() % palette.size
        val next = (base + 1) % palette.size
        val localT = phase - floor(phase)
        val topColor = lerp(
            palette[base],
            palette[next],
            FastOutSlowInEasing.transform(localT)
        )

        // Clamp stops
        val startStop = (blueFraction - 0.05f).coerceIn(0f, 1f)
        val midStop = blueFraction.coerceIn(0f, 1f)

        // Gradient geometry
        val angleRad = angleDeg * PI.toFloat() / 180f
        val length = hypot(wPx.toDouble(), hPx.toDouble()).toFloat()
        val dx = cos(angleRad) * length
        val dy = sin(angleRad) * length
        val start = Offset(wPx / 2f - dx / 2f, hPx / 2f - dy / 2f)
        val end = Offset(wPx / 2f + dx / 2f, hPx / 2f + dy / 2f)

        val swayingBrush = Brush.linearGradient(
            colorStops = arrayOf(
                0f        to Color.White,
                startStop to Color.White.copy(alpha = 0.30f),
                midStop   to topColor,
                1f        to topColor.copy(alpha = 0.60f)
            ),
            start = start,
            end = end
        )

        // Background layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = swayingBrush)
        )

        // Haze overlay layer (radius adapts to screen)
        val hazeRadius = max(wPx, hPx) * 0.9f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        radius = hazeRadius
                    )
                )
        )
    }
}