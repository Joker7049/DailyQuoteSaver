package org.example.dailyquotesaver.ui.theme



// Define the FontFamily using the bundled font
// In your Type.kt file

// These are the imports you should have at the top of the file
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dailyquotesaver.composeapp.generated.resources.GideonRoman_Regular
import dailyquotesaver.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font


// Default Material 3 typography values (can be used as a base if needed)
// val baseline = Typography()


@Composable
fun AppTypography() : Typography{

    val GideonRomanFontFamily = FontFamily(
        // 2. We can now call the @Composable Font() function here.
        Font(
            resource = Res.font.GideonRoman_Regular, // Your font file name
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    )

    val SerifFontFamily = FontFamily.Serif // Added this line

    return Typography(
        displayLarge = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal, // Or FontWeight.Bold if appropriate for titles
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Medium, // Material Design often uses Medium weight for titles
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = SerifFontFamily, // Changed here
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}
