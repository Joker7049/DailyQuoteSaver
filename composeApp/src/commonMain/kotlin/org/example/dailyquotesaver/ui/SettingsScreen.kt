package org.example.dailyquotesaver.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
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
import org.example.dailyquotesaver.data.ApiKeyRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(apiKeyRepository: ApiKeyRepository, onNavigateBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf<String?>(null) }
    var apiKeyInput by remember(apiKey) { mutableStateOf(apiKey ?: "") }

    LaunchedEffect(Unit) {
        apiKey = apiKeyRepository.getApiKey()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("API Key Settings")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text("Gemini API Key") },
            placeholder = { Text("Enter your API key") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    apiKeyRepository.saveApiKey(apiKeyInput)
                }
            }
        ) {
            Text("Save")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            if (apiKey.isNullOrBlank()) {
                "No API Key saved."
            } else {
                "API Key is saved."
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}
