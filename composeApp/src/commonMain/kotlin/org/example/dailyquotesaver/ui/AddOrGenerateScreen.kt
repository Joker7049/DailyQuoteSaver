// in org/example/dailyquotesaver/ui/AddOrGenerateScreen.kt

package org.example.dailyquotesaver.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class AddMode { MANUAL, AI }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrGenerateScreen(
    generateUiState: GenerateUiState,
    onSaveQuote: (text: String, author: String, tags: String) -> Unit,
    onGenerateQuote: (prompt: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var mode by remember { mutableStateOf(AddMode.MANUAL) }

    // State for the manual form fields
    var manualQuoteText by remember { mutableStateOf("") }
    var manualAuthor by remember { mutableStateOf("") }
    var manualTags by remember { mutableStateOf("") }

    // State for the AI prompt field
    var prompt by remember { mutableStateOf("") }

    // This effect reacts when the AI successfully generates a quote.
    // It pre-fills the manual form and switches the view.
    LaunchedEffect(generateUiState) {
        if (generateUiState is GenerateUiState.Success) {
            manualQuoteText = generateUiState.quote
            mode = AddMode.MANUAL
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add a New Quote") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Mode Selector ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = mode == AddMode.MANUAL, onClick = { mode = AddMode.MANUAL })
                Text("Write Manually", modifier = Modifier.padding(start = 4.dp, end = 16.dp))
                RadioButton(selected = mode == AddMode.AI, onClick = { mode = AddMode.AI })
                Text("Generate with AI", modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(Modifier.height(24.dp))

            // --- Conditional UI ---
            when (mode) {
                AddMode.MANUAL -> ManualEntryForm(
                    quoteText = manualQuoteText,
                    onQuoteTextChange = { manualQuoteText = it },
                    author = manualAuthor,
                    onAuthorChange = { manualAuthor = it },
                    tags = manualTags,
                    onTagsChange = { manualTags = it },
                    onSaveClick = { onSaveQuote(manualQuoteText, manualAuthor, manualTags) }
                )
                AddMode.AI -> AiGenerationForm(
                    prompt = prompt,
                    onPromptChange = { prompt = it },
                    uiState = generateUiState,
                    onGenerateClick = { onGenerateQuote(prompt) }
                )
            }
        }
    }
}


// --- Private Helper Composables for Cleanliness ---

@Composable
private fun ManualEntryForm(
    quoteText: String, onQuoteTextChange: (String) -> Unit,
    author: String, onAuthorChange: (String) -> Unit,
    tags: String, onTagsChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = quoteText, onValueChange = onQuoteTextChange, label = { Text("Quote") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = author, onValueChange = onAuthorChange, label = { Text("Author (optional)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = tags, onValueChange = onTagsChange, label = { Text("Tags (comma-separated)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = onSaveClick, enabled = quoteText.isNotBlank()) {
            Text("Save Quote")
        }
    }
}

@Composable
private fun AiGenerationForm(
    prompt: String,
    onPromptChange: (String) -> Unit,
    uiState: GenerateUiState,
    onGenerateClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            label = { Text("Enter a feeling or keyword (e.g., 'hope')") },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is GenerateUiState.Loading
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onGenerateClick, enabled = prompt.isNotBlank() && uiState !is GenerateUiState.Loading) {
            Text("Generate")
        }
        Spacer(Modifier.height(24.dp))

        when (uiState) {
            is GenerateUiState.Loading -> CircularProgressIndicator()
            is GenerateUiState.Error -> Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
            is GenerateUiState.Success -> Text(
                "Quote generated! Switched to manual mode for you to edit and save.",
                style = MaterialTheme.typography.bodySmall
            )
            is GenerateUiState.Idle -> { /* Nothing to show */ }
        }
    }
}