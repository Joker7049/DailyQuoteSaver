package org.example.dailyquotesaver.ui


import QuoteCard
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.dailyquotesaver.Quote

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuoteList(
    quotes: List<Quote>,
    onFavoriteClick: (Long) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onTagClick: (String) -> Unit,
    onEditClick: (Quote) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemSpacing: Dp = 8.dp,
    searchQuery: String? = null,
    // If you want to customize how items are keyed; defaults to quote.id
    key: (Quote) -> Any = { it.id }, // assuming Quote has a stable id: Long
    emptyContent: @Composable (() -> Unit)? = null,
    animateItems: Boolean = true
) {
    if (quotes.isEmpty()) {
        if (emptyContent != null) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) { emptyContent() }
        } else {
            // Default empty state
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nothing here yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize(animationSpec = spring()),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(
            items = quotes,
            key = key
        ) { quote ->
            QuoteCard(
                quote = quote,
                searchQuery = searchQuery.orEmpty(),
                onTagClick = onTagClick,
                onDeleteRequest = onDeleteRequest,
                onEditClick = onEditClick,
                onFavoriteClick = onFavoriteClick,
                modifier = if (animateItems) Modifier.animateItem() else Modifier
            )
        }
    }
}