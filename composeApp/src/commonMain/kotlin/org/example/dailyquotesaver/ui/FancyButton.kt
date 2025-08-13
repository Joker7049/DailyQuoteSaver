
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FancyButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(Color(0xFF4CAF50), Color(0xFF81C784)),
    textColor: Color = Color.White,
    fontSize: TextUnit = 18.sp,
    shape: Shape = RoundedCornerShape(25.dp)
) {
    // how "faded" the disabled state looks
    val disabledAlpha = 0.35f

    // target colors depend on enabled state
    val targetColors = if (enabled) gradientColors else gradientColors.map { it.copy(alpha = disabledAlpha) }
    // animate each color so transitions look smooth
    val animatedColors = targetColors.map { animateColorAsState(targetValue = it).value }

    // animate elevation so disabled state looks "flatter"
    val elevation by animateDpAsState(targetValue = if (enabled) 8.dp else 2.dp)

    // animate text color fade
    val curTextColor = animateColorAsState(targetValue = if (enabled) textColor else textColor.copy(alpha = 0.7f)).value

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth(),
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = elevation, shape = shape)
                .background(brush = Brush.horizontalGradient(animatedColors), shape = shape)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (enabled){
                Text(
                    text = text,
                    color = curTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize
                )
            }else{
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = ""
                )
            }
        }
    }
}
