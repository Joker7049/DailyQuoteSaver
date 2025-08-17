// in org/example/dailyquotesaver/ui/AddOrGenerateScreen.kt

package org.example.dailyquotesaver.ui

import FancyButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class AddMode { MANUAL, AI }

private data class ManualEntryFormState(
    val quoteText: String = "",
    val author: String = "",
    val tags: String = ""
)

import org.example.dailyquotesaver.data.ApiKeyRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.AlertDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrGenerateScreen(
    generateUiState: GenerateUiState,
    onSaveQuote: (text: String, author: String, tags: String) -> Unit,
    onGenerateQuote: (prompt: String) -> Unit,
    onNavigateBack: () -> Unit,
    resetGenerateUiState: () -> Unit,
    apiKeyRepository: ApiKeyRepository,
    onNavigateToSettings: () -> Unit
) {
    var mode by remember { mutableStateOf(AddMode.MANUAL) }
    var manualFormState by remember { mutableStateOf(ManualEntryFormState()) }
    var prompt by remember { mutableStateOf("") } // State for the AI prompt field
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("API Key Required") },
            text = { Text("Please set your OpenAI API key in the settings to use the AI generation feature.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onNavigateToSettings()
                    }
                ) {
                    Text("Go to Settings")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
                RadioButton(
                    selected = mode == AddMode.AI,
                    onClick = {
                        scope.launch {
                            if (apiKeyRepository.getApiKey().isNullOrBlank()) {
                                showDialog = true
                            } else {
                                mode = AddMode.AI
                            }
                        }
                    }
                )
                Text("Generate with AI", modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(Modifier.height(24.dp))

            // --- Conditional UI ---
            when (mode) {
                AddMode.MANUAL -> ManualEntryForm(
                    formState = manualFormState,
                    onFormStateChange = { manualFormState = it },
                    onSaveClick = {
                        onSaveQuote(
                            manualFormState.quoteText,
                            manualFormState.author,
                            manualFormState.tags
                        )
                    }
                )

                AddMode.AI -> AiGenerationForm(
                    prompt = prompt,
                    onPromptChange = { prompt = it },
                    uiState = generateUiState,
                    onGenerateClick = {
                        scope.launch {
                            if (apiKeyRepository.getApiKey().isNullOrBlank()) {
                                showDialog = true
                            } else {
                                onGenerateQuote(prompt)
                            }
                        }
                    },
                    onAcceptQuote = { generatedQuote ->
                        manualFormState =
                            ManualEntryFormState(quoteText = generatedQuote) // Clears author and tags
                        mode = AddMode.MANUAL
                    },
                    onClearPrompt = {
                        prompt = ""
                        resetGenerateUiState()
                    }
                )
            }
        }
    }
}

// --- Private Helper Composables for Cleanliness ---

@Composable
private fun ManualEntryForm(
    formState: ManualEntryFormState,
    onFormStateChange: (ManualEntryFormState) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = formState.quoteText,
            onValueChange = { onFormStateChange(formState.copy(quoteText = it)) },
            label = { Text("Quote") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = formState.author,
            onValueChange = { onFormStateChange(formState.copy(author = it)) },
            label = { Text("Author (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = formState.tags,
            onValueChange = { onFormStateChange(formState.copy(tags = it)) },
            label = { Text("Tags (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        FancyButton(
            text = "Save Quote",
            onClick = onSaveClick,
            enabled = formState.quoteText.isNotBlank(),
            modifier = Modifier.size(height = 50.dp, width = 120.dp)
        )
    }
}

@Composable
private fun AiGenerationForm(
    prompt: String,
    onPromptChange: (String) -> Unit,
    uiState: GenerateUiState,
    onGenerateClick: () -> Unit,
    onAcceptQuote: (String) -> Unit,
    onClearPrompt: () -> Unit
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
        AnimatedVisibility(
            visible = uiState is GenerateUiState.Idle,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ){
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FancyButton(
                    text = "Generate",
                    onClick = onGenerateClick,
                    enabled = prompt.isNotBlank(),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        when (uiState) {
            is GenerateUiState.Loading -> CircularProgressIndicator()
            is GenerateUiState.Error -> {
                AiErrorView(
                    errorMessage = uiState.message,
                    onTryAgain = { onGenerateClick() }
                )
            }
            is GenerateUiState.Success -> {
                AiSuccessView(
                    generatedQuote = uiState.quote,
                    onGenerateAgain = { onGenerateClick() },
                    onUseQuote = {
                        onAcceptQuote(uiState.quote)
                        onClearPrompt() // This was already here, makes sense to keep
                    }
                )
            }
            is GenerateUiState.Idle -> { /* Nothing to show */ }
        }
    }
}


@Composable
fun AiSuccessView(
    modifier: Modifier = Modifier,
    generatedQuote: String,
    onGenerateAgain: () -> Unit,
    onUseQuote: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ){
        OutlinedTextField(
            value = generatedQuote,
            onValueChange = {},
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ){
            Button(onClick = { onGenerateAgain() }) {
                Text("Generate Again")
            }
            Button(onClick = { onUseQuote() }) {
                Text("Use This Quote")
            }
        }
    }
}


@Composable
fun AiErrorView(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onTryAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(errorMessage, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onTryAgain() }) {
            Text("Try Again")
        }
    }
}
