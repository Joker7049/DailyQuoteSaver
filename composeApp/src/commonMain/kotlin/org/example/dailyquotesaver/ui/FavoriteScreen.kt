package org.example.dailyquotesaver.ui

// in org/example/dailyquotesaver/ui/FavoritesScreen.kt

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.dailyquotesaver.Quote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    // This screen receives the list of quotes that are ALREADY filtered to be favorites.
    favoriteQuotes: List<Quote>,

    // This is the same function from our main screen for when a heart icon is clicked.
    onFavoriteClick: (Long) -> Unit,

    onDeleteRequest: (Long) -> Unit,

    // A new function to signal that we want to go back to the previous screen.
    onBack: () -> Unit,
    onTagClick: (String) -> Unit,
    onEditClick: (Quote) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Quotes") },
                navigationIcon = {
                    // We need an icon button here that shows a back arrow.
                    // When it's clicked, what should it do?
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // We need a list that can scroll. LazyColumn is perfect for this.
        // It takes the list of favorite quotes and creates a row for each one.
        LazyColumn(modifier = Modifier.padding(paddingValues)) {

            items(favoriteQuotes) { quote ->
                // For each 'quote' in our list, what Composable should we display?
                // Think about the reusable component we just built.
                // It needs two parameters passed to it.

                QuoteCard(
                    quote = quote,
                    onFavoriteClick = onFavoriteClick,
                    onDeleteRequest = onDeleteRequest,
                    onTagClick = onTagClick,
                    onEditClick = onEditClick
                )

                /*
                 * YOUR CODE HERE
                 *
                 * Hint: You need to call a Composable function.
                 * It takes a `quote` object and an `onFavoriteClick` lambda.
                 */
            }
        }
    }
}