package org.example.dailyquotesaver.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun buildHighlightedText(
    text: String,
    query: String,
    highlightColor: Color = MaterialTheme.colorScheme.primary
): AnnotatedString {
    return buildAnnotatedString {
        if (query.isBlank()) {
            append(text)
            return@buildAnnotatedString
        }

        var startIndex = text.indexOf(query, ignoreCase = true)
        var lastIndex = 0

        while (startIndex != -1) {
            // Append the text before the match
            append(text.substring(lastIndex, startIndex))

            // Append the matched text with a special style
            withStyle(
                style = SpanStyle(
                    color = highlightColor,
                    fontWeight = FontWeight.Bold,
                    background = Color.Yellow.copy(alpha = 0.3f)
                )
            ) {
                // CORRECTED CODE: The end index is startIndex + query.length
                append(text.substring(startIndex, startIndex + query.length))
            }

            // Update our indices
            lastIndex = startIndex + query.length
            startIndex = text.indexOf(query, startIndex = lastIndex, ignoreCase = true)
        }

        // CORRECTED CODE: Append the rest of the string
        if (lastIndex < text.length) {
            append(text.substring(lastIndex, text.length))
        }
    }
}