package org.example.dailyquotesaver.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.dailyquotesaver.Quote
import org.example.dailyquotesaver.data.QuoteRepository

@Composable
fun QuoteScreen(repository: QuoteRepository) {
    // Create a coroutine scope tied to composition
    val scope = rememberCoroutineScope()

    // UI state holding the current quote
    var currentQuote by remember { mutableStateOf<Quote?>(null) }

    // When the screen first appears, load a random quote
    LaunchedEffect(Unit) {
        currentQuote = repository.getQuotes().randomOrNull()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentQuote != null) {
            Text("💬 \"${currentQuote!!.text}\"", style = MaterialTheme.typography.bodyLarge)
            currentQuote!!.author?.let { Text("— $it", style = MaterialTheme.typography.titleSmall) }
        } else {
            Text("No quotes saved yet.", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        var inputText by remember { mutableStateOf("") }
        var inputAuthor by remember { mutableStateOf("") }

        BasicTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Box(Modifier.padding(8.dp)) {
                    if (inputText.isEmpty()) Text("Enter quote…")
                    inner()
                }
            }
        )

        BasicTextField(
            value = inputAuthor,
            onValueChange = { inputAuthor = it },
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Box(Modifier.padding(8.dp)) {
                    if (inputAuthor.isEmpty()) Text("Enter author (optional)…")
                    inner()
                }
            }
        )

        Button(onClick = {
            if (inputText.isNotBlank()) {
                scope.launch {
                    repository.saveQuote(Quote(inputText.trim(), inputAuthor.trim().ifEmpty { null }))
                    inputText = ""
                    inputAuthor = ""
                    currentQuote = repository.getQuotes().randomOrNull()
                }
            }
        }) {
            Text("Save Quote")
        }
    }
}
