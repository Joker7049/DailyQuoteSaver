package org.example.dailyquotesaver.ui

import FancyButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
 * Composable function for the content of the quote screen.
 * Displays a list of quotes, a search bar, and a tag filter banner.
 * This composable is meant to be used as the content of a Scaffold.
 *
 * @param quotes List of all quotes to display.
 * @param repository Repository for interacting with quote data.
 * @param searchQuery Current search query.
 * @param onSearchQueryChange Callback for when the search query changes.
 * @param activeTagFilter Currently active tag filter.
 * @param onTagClick Callback for when a tag is clicked.
 * @param onClearTagFilter Callback to clear the active tag filter.
 * @param onDeleteRequest Callback to request deletion of a quote.
 * @param onEditClick Callback for when the edit action for a quote is clicked.
 * @param contentPadding Padding to apply to the root layout, typically from a Scaffold.
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
    onDeleteRequest: (Long) -> Unit,
    onEditClick: (Quote) -> Unit,

) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier

            .padding(horizontal = 16.dp)
    ) {
        SearchField(
            query = searchQuery,
            onQueryChange = onSearchQueryChange
        )

        TagFilterBanner(
            activeTag = activeTagFilter,
            onClear = onClearTagFilter
        )

        QuoteList(
            quotes = quotes,
            searchQuery = searchQuery,
            onTagClick = onTagClick,
            onDeleteRequest = onDeleteRequest,
            onEditClick = onEditClick,
            onFavoriteClick = { quoteId ->
                scope.launch { repository.toggleFavorite(quoteId) }
            },
            contentPadding = PaddingValues(0.dp) // Internal padding for the list
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteTopBar(onGoToFavorites: () -> Unit, onGoToSettings: () -> Unit) { // Made public
    TopAppBar(
        title = { Text("My Quotes") },
        actions = {
            FancyButton(
                text = "Favorites",
                onClick = onGoToFavorites,
                modifier = Modifier.size(width = 100.dp, height = 50.dp)
            )
            IconButton(onClick = onGoToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}


@Composable
fun AddQuoteFab(onAddClick: () -> Unit) { // Made public
    FloatingActionButton(onClick = onAddClick) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add a new quote"
        )
    }
}


@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search by text or author") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") }
    )
}


@Composable
private fun TagFilterBanner(
    activeTag: String?,
    onClear: () -> Unit
) {
    if (activeTag != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Filtering by: #$activeTag")
            Button(onClick = onClear) {
                Text("Clear")
            }
        }
    }
}
