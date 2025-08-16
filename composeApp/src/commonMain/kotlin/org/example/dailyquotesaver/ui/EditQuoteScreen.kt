package org.example.dailyquotesaver.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.dailyquotesaver.Quote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQuoteScreen(
    quoteToEdit: Quote,
    onSaveClick: (Quote) -> Unit,
    modifier: Modifier = Modifier // Added modifier parameter
) {
    var newQuoteText by remember { mutableStateOf(quoteToEdit.text) }
    var newQuoteAuthor by remember { mutableStateOf(quoteToEdit.author.orEmpty()) }
    var newQuoteTags by remember { mutableStateOf(quoteToEdit.tags.joinToString(",")) }

    Column(
        modifier = modifier // Apply the modifier passed from the parent (e.g., App.kt's Scaffold)
            .fillMaxSize()     // Column fills the area provided by the parent
    ) {
        QuoteForm(
            quoteText = newQuoteText,
            author = newQuoteAuthor,
            tags = newQuoteTags,
            onQuoteTextChange = { newQuoteText = it },
            onAuthorChange = { newQuoteAuthor = it },
            onTagsChange = { newQuoteTags = it },
            isSaveEnabled = true, // additional external conditions if you have them
            onSaveClick = {
                val text = newQuoteText.trim()
                if (text.isBlank()) return@QuoteForm // guard (button already disabled, but safe)

                val tagList = newQuoteTags
                    .split(',')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                val updated = quoteToEdit.copy(
                    text = text,
                    author = newQuoteAuthor.trim().ifBlank { null },
                    tags = tagList
                )
                onSaveClick(updated)
            },
            modifier = Modifier
                .fillMaxWidth() // QuoteForm fills available width
                .weight(1f)     // QuoteForm takes remaining vertical space in the Column
                .padding(horizontal = 16.dp) // Specific horizontal padding for the form's content
        )
    }
}


@Composable
fun QuoteForm(
    quoteText: String,
    author: String,
    tags: String,
    onQuoteTextChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onTagsChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    isSaveEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Quote with validation ---
        val isQuoteError = quoteText.isBlank()
        OutlinedTextField(
            value = quoteText,
            onValueChange = onQuoteTextChange,
            label = { Text("Quote") },
            isError = isQuoteError,
            supportingText = {
                if (isQuoteError) {
                    Text(
                        text = "Quote cannot be empty",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Author (optional)
        OutlinedTextField(
            value = author,
            onValueChange = onAuthorChange,
            label = { Text("Author (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Tags (comma-separated)
        OutlinedTextField(
            value = tags,
            onValueChange = onTagsChange,
            label = { Text("Tags (comma-separated)") },
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onSaveClick,
            enabled = !isQuoteError && isSaveEnabled,
        ) {
            Text("Save")
        }
    }
}
