package org.example.dailyquotesaver.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.dailyquotesaver.Quote
import org.example.dailyquotesaver.data.QuoteRepository

/**
 * Composable function for the main screen of the application.
 * Displays a list of quotes, a search bar, a button to go to favorites,
 * and a floating action button to add new quotes.
 *
 * It also includes a dialog for adding new quotes, which is shown when
 * the floating action button is clicked.
 *
 * @param allQuotes List of all quotes to display.
 * @param repository Repository for interacting with quote data.
 * @param onGoToFavorites Callback function to navigate to the favorites screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteScreen(
    quotes: List<Quote>,
    repository: QuoteRepository,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeTagFilter: String?,
    onTagClick: (String) -> Unit,
    onClearTagFilter: () -> Unit,
    onGoToFavorites: () -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onEditClick: (Quote) -> Unit,
    onAddClick: () -> Unit
) {
    val scope = rememberCoroutineScope()









    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Quotes") },
                actions = {
                    Button(onClick = onGoToFavorites) {
                        Text("Favorites")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "adding a new quote"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                value = searchQuery,
                onValueChange = { onSearchQueryChange(it) },
                label = { Text("Search by text or author") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") }
            )

            if (activeTagFilter != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Filtering by: #$activeTagFilter")
                    Button(onClick = { onClearTagFilter() }) { // <-- Use the lambda
                        Text("Clear")
                    }
                }
            }

            LazyColumn {
                items(quotes) { quote ->
                    QuoteCard(
                        quote = quote,
                        onFavoriteClick = { quoteId ->
                            scope.launch {
                                repository.toggleFavorite(quoteId)
                            }
                        },
                        onDeleteRequest = onDeleteRequest,
                        searchQuery = searchQuery,
                        onTagClick = onTagClick,
                        onEditClick = onEditClick
                    )
                }
            }
        }
    }
}