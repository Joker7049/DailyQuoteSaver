package org.example.dailyquotesaver.ui



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.dailyquotesaver.Quote

/**
 * Displays the screen for managing favorite quotes.
 *
 * This screen shows a list of favorite quotes. If there are no favorite quotes,
 * a message indicating that is displayed. Users can interact with the quotes
 * by clicking on them to toggle their favorite status, deleting them, clicking on tags,
 * or editing them.
 *
 * @param favoriteQuotes The list of quotes marked as favorites.
 * @param onFavoriteClick A callback function invoked when the favorite icon of a quote is clicked.
 *                        It receives the ID of the quote.
 * @param onDeleteRequest A callback function invoked when a request to delete a quote is made.
 *                        It receives the ID of the quote to be deleted.
 * @param onBack A callback function invoked when the back button in the top app bar is clicked.
 * @param onTagClick A callback function invoked when a tag on a quote card is clicked.
 *                   It receives the tag string.
 * @param onEditClick A callback function invoked when the edit action for a quote is clicked.
 *                    It receives the [Quote] object to be edited.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoriteQuotes: List<Quote>,
    onFavoriteClick: (Long) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onBack: () -> Unit,
    onTagClick: (String) -> Unit,
    onEditClick: (Quote) -> Unit
) {
    Scaffold(
        topBar = { FavoritesTopBar(onBack) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (favoriteQuotes.isEmpty()) {
                EmptyFavoritesMessage()
            } else {
                QuoteList(
                    quotes = favoriteQuotes,
                    searchQuery = null, // no highlight needed here (or pass a value if you want)
                    onFavoriteClick = onFavoriteClick,
                    onDeleteRequest = onDeleteRequest,
                    onTagClick = onTagClick,
                    onEditClick = onEditClick,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    ),
                    emptyContent = { EmptyFavoritesMessage() }
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Favorite Quotes") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}


@Composable
private fun EmptyFavoritesMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No favorite quotes yet.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

