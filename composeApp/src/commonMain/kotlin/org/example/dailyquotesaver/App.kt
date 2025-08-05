package org.example.dailyquotesaver

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.example.dailyquotesaver.data.QuoteRepository
import org.example.dailyquotesaver.network.GeminiQuoteService
//import org.example.dailyquotesaver.network.GeminiQuoteService
import org.example.dailyquotesaver.ui.AddOrGenerateScreen
import org.example.dailyquotesaver.ui.EditQuoteScreen
import org.example.dailyquotesaver.ui.FavoritesScreen
import org.example.dailyquotesaver.ui.GenerateUiState
import org.example.dailyquotesaver.ui.QuoteScreen
import org.jetbrains.compose.ui.tooling.preview.Preview


enum class Screen { QUOTE, FAVORITES, Edit, ADD_OR_GENERATE }


@Composable
@Preview
fun App(repo: QuoteRepository) {
    var currentScreen by remember { mutableStateOf(Screen.QUOTE) }
    val allQuotes by repo.quotes.collectAsState(emptyList())
    val scope = rememberCoroutineScope()



    val aiService = remember { GeminiQuoteService() }
    var generateUiState by remember { mutableStateOf<GenerateUiState>(GenerateUiState.Idle) }

    var quoteToDelete: Quote? by remember { mutableStateOf(null) }


    val onDeleteRequestHandler: (Long) -> Unit = { quoteId ->
        quoteToDelete = allQuotes.find { it.id == quoteId }
    }

    var searchQuery by remember { mutableStateOf("") }
    var activeTagFilter by remember { mutableStateOf<String?>(null) }

    var editedQuote by remember { mutableStateOf<Quote?>(null) }

    val filteredQuotes = remember(allQuotes, searchQuery, activeTagFilter) {
        var quotes = allQuotes
        if (searchQuery.isBlank()) {
            quotes
        } else {
            quotes = quotes.filter {
                val query = searchQuery.trim()
                it.text.contains(query, ignoreCase = true) ||
                        it.author?.contains(query, ignoreCase = true) == true
            }
        }

        if (activeTagFilter == null) {
            quotes
        } else {
            quotes = quotes.filter {
                it.tags.contains(activeTagFilter)
            }
        }
        quotes

    }
    MaterialTheme {

        if (quoteToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    /*
                     * This is called when the user clicks outside the dialog
                     * or presses the back button. We should cancel the delete.
                     * What should you set `quoteToDelete` to?
                     */
                    quoteToDelete = null
                },
                title = { Text("Confirm Deletion") },
                text = {
                    /*
                     * Show a message to the user. Make sure to use the quote's text
                     * from the `quoteToDelete` state variable so they know what they
                     * are deleting.
                     * Example: Text("Are you sure you want to delete this quote: \"${quoteToDelete?.text}\"?")
                     */
                    Text("Are you sure you want to delete this quote: \"${quoteToDelete?.text}\"?")
                },
                confirmButton = {
                    Button(onClick = {
                        /*
                         * The user confirmed.
                         * 1. Use the `scope` to launch a coroutine.
                         * 2. Inside the coroutine, call the repository's `deleteQuote` function.
                         *    You'll need the ID from the `quoteToDelete` state variable.
                         * 3. After calling the repository, hide the dialog.
                         */
                        scope.launch {
                            repo.deleteQuote(quoteToDelete!!.id)
                            quoteToDelete = null
                        }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        quoteToDelete = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }




        when (currentScreen) {
            Screen.QUOTE -> QuoteScreen(
                repository = repo,
                quotes = filteredQuotes,
                onGoToFavorites = { currentScreen = Screen.FAVORITES },
                onDeleteRequest = onDeleteRequestHandler,
                searchQuery = searchQuery,
                activeTagFilter = activeTagFilter,
                onTagClick = { tag -> activeTagFilter = tag },
                onClearTagFilter = { activeTagFilter = null },
                onSearchQueryChange = { newQuery -> searchQuery = newQuery },
                onEditClick = { quote ->
                    editedQuote = quote
                    currentScreen = Screen.Edit
                },
                onAddClick = {
                    // When starting, always reset the AI state to Idle
                    generateUiState = GenerateUiState.Idle
                    currentScreen = Screen.ADD_OR_GENERATE
                }
            )

            Screen.FAVORITES -> FavoritesScreen(
                favoriteQuotes = allQuotes.filter { it.isFavorite },
                onFavoriteClick = {
                    scope.launch {
                        repo.toggleFavorite(it)
                    }
                },
                onDeleteRequest = onDeleteRequestHandler,
                onBack = { currentScreen = Screen.QUOTE },
                onTagClick = { tag -> activeTagFilter = tag },
                onEditClick = { quote ->
                    editedQuote = quote
                    currentScreen = Screen.Edit
                }
            )

            Screen.Edit -> if (editedQuote != null) {
                EditQuoteScreen(
                    quoteToEdit = editedQuote!!,
                    onSaveClick = { quote ->
                        scope.launch {
                            repo.updateQuote(quote)
                            currentScreen = Screen.QUOTE
                        }
                    },
                    onCancelClick = {
                        currentScreen = Screen.QUOTE
                    }
                )
            } else {
                currentScreen = Screen.QUOTE
            }
            Screen.ADD_OR_GENERATE -> AddOrGenerateScreen(
                generateUiState = generateUiState,
                onNavigateBack = { currentScreen = Screen.QUOTE },
                onGenerateQuote = { prompt ->
                    scope.launch {
                        generateUiState = GenerateUiState.Loading
                        val result = aiService.generateQuote(prompt)
                        generateUiState = if (result.startsWith("Sorry,")) {
                            GenerateUiState.Error(result)
                        } else {
                            GenerateUiState.Success(result)
                        }
                    }
                },
                onSaveQuote = { text, author, tags ->
                    scope.launch {
                        val tagsList = tags.split(',')
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        val newQuote = Quote(
                            text = text,
                            author = author.ifBlank { null },
                            tags = tagsList
                        )
                        repo.saveQuote(newQuote)
                        currentScreen = Screen.QUOTE // Navigate back after saving
                    }
                }
            )

        }

    }
}