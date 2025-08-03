package org.example.dailyquotesaver.ui





// This sealed interface represents all possible states of the AI generation UI.
// It will be created in App.kt and passed down to the screen.
sealed interface GenerateUiState {
    data object Idle : GenerateUiState
    data object Loading : GenerateUiState
    data class Success(val quote: String) : GenerateUiState
    data class Error(val message: String) : GenerateUiState
}