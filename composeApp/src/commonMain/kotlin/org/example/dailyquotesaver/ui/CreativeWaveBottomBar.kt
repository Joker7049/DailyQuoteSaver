package org.example.dailyquotesaver.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

// Definition for a navigation item
/*data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector, // Icon to display
    val route: String // Navigation route
)*/

// Custom Shape for the wavy effect
class WaveBottomBarShape(private val dip: Float = 0.15f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f) // Start top left
            lineTo(size.width, 0f) // Line to top right
            lineTo(size.width, size.height) // Line to bottom right
            lineTo(0f, size.height) // Line to bottom left

            // The "wave" part
            // Move to the start of the wave
            moveTo(x = size.width, y = 0f)
            // Draw a quadratic bezier curve for the dip
            quadraticBezierTo(
                x1 = size.width * 0.5f, // Control point X (center)
                y1 = size.height * dip * 2, // Control point Y (how deep it dips)
                x2 = 0f, // End point X (left side)
                y2 = 0f // End point Y
            )
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun CreativeWaveBottomBar(
    navItems: List<BottomNavItem>, // Accept nav items as a parameter
    currentRoute: String?,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // val navItems = listOf() // Removed: navItems are now passed as a parameter

    Box(
        modifier = modifier
            .shadow(elevation = 10.dp, shape = WaveBottomBarShape())
            .graphicsLayer {
                // This is crucial for applying the custom shape
                shape = WaveBottomBarShape()
                clip = true
            }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )
            )
            .height(64.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = item.route == currentRoute
                val iconSize by animateDpAsState(targetValue = if (isSelected) 32.dp else 24.dp)

                IconButton(onClick = { onTabSelected(item.route) }) {
                    Icon(
                        imageVector = item.selectedIcon, // Always use filled for this design
                        contentDescription = item.label,
                        modifier = Modifier.size(iconSize),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}