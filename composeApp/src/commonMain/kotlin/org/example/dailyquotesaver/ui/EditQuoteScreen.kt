package org.example.dailyquotesaver.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
    onCancelClick: () -> Unit
) {

    var newQuoteText by remember { mutableStateOf(quoteToEdit.text) }
    var newQuoteAuthor by remember { mutableStateOf(quoteToEdit.author ?: "") }
    var newQuoteTags by remember { mutableStateOf(quoteToEdit.tags.joinToString(",")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Quote") },
                navigationIcon = {
                    IconButton(onClick = onCancelClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = newQuoteText,
                onValueChange = { newQuoteText = it },
                label = { Text("Quote") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newQuoteAuthor,
                onValueChange = { newQuoteAuthor = it },
                label = { Text("Author (optional)") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newQuoteTags,
                onValueChange = { newQuoteTags = it },
                label = { Text("Tags (comma-separated)") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val tagList = if (newQuoteTags.isNotBlank()) {
                        newQuoteTags.split(',')
                            .map { tag ->
                                tag.trim()
                            }
                            .filter { tag ->
                                tag.isNotBlank()
                            }
                    } else {
                        emptyList<String>()
                    }
                    val updatedQuote = quoteToEdit.copy(text = newQuoteText, author = newQuoteAuthor.ifBlank { null }, tags = tagList)
                    onSaveClick(updatedQuote)
                },
                enabled = newQuoteText.isNotBlank()
            ) {
                Text("Save")
            }

        }

    }

}