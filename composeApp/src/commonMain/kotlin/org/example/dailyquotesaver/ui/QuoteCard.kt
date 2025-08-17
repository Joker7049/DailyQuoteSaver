import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.dailyquotesaver.Quote
import org.example.dailyquotesaver.ui.buildHighlightedText
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuoteCard(
    quote: Quote,
    onFavoriteClick: (Long) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onTagClick: (String) -> Unit,
    onEditClick: (Quote) -> Unit,
    modifier: Modifier = Modifier,
    searchQuery: String = ""
) {
    val entryAnim = rememberQuoteEntryAnimation()
    val favoriteColor = animateFavoriteColor(quote.isFavorite)
    val dismissState = rememberDismissState(
        onEdit = { onEditClick(quote) },
        onDelete = { onDeleteRequest(quote.id) }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState) },
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .alpha(entryAnim.alpha)
            .offset(y = entryAnim.offsetY.dp)
            .fillMaxWidth()
    ) {
        QuoteCardContent(
            quote = quote,
            searchQuery = searchQuery,
            favoriteColor = favoriteColor,
            onFavoriteClick = { onFavoriteClick(quote.id) },
            onTagClick = onTagClick
        )
    }
}

@Preview()
@Composable
fun QuoteCardPreview() {
    val sampleQuotes = listOf(
        Quote(
            id = 1,
            text = "This is the first sample quote for preview purposes.",
            author = "Preview Author 1",
            isFavorite = true,
            tags = listOf("sample", "preview", "tag1")
        ),
        Quote(
            id = 2,
            text = "A second quote to demonstrate the list.",
            author = "Preview Author 2",
            isFavorite = false,
            tags = listOf("sample", "list", "tag2")
        ),
        Quote(
            id = 3,
            text = "Yet another quote with more tags.",
            author = "Another Previewer",
            isFavorite = true,
            tags = listOf("preview", "tag3", "moretags", "example")
        )
    )
    LazyColumn {
        items(sampleQuotes) { quote ->
            QuoteCard(
                quote = quote,
                onFavoriteClick = {},
                onDeleteRequest = {},
                onTagClick = {},
                onEditClick = {},
                searchQuery = "sample"
            )
        }
    }
}


@Composable
private fun rememberQuoteEntryAnimation(): QuoteCardAnimation {
    var isVisible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500, easing = EaseOut)
    )
    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = tween(500, easing = EaseOut)
    )

    LaunchedEffect(Unit) { isVisible = true }

    return QuoteCardAnimation(alpha, offsetY)
}

private data class QuoteCardAnimation(val alpha: Float, val offsetY: Float)


@Composable
private fun animateFavoriteColor(isFavorite: Boolean): Color {
    val transition = updateTransition(targetState = isFavorite, label = "favoriteTransition")
    return transition.animateColor(
        transitionSpec = { tween(durationMillis = 200) },
        label = "favoriteColor"
    ) { favorite ->
        if (favorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
    }.value
}

@Composable
private fun rememberDismissState(
    onEdit: () -> Unit,
    onDelete: () -> Unit
): SwipeToDismissBoxState {
    val state = rememberSwipeToDismissBoxState(SwipeToDismissBoxValue.Settled)

    LaunchedEffect(state.currentValue) {
        when (state.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                onEdit(); state.reset()
            }

            SwipeToDismissBoxValue.EndToStart -> {
                onDelete(); state.reset()
            }

            else -> {}
        }
    }
    return state
}


@Composable
private fun QuoteCardContent(
    quote: Quote,
    searchQuery: String,
    favoriteColor: Color,
    onFavoriteClick: () -> Unit,
    onTagClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = buildHighlightedText(quote.text, searchQuery),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (!quote.author.isNullOrBlank()) {
                Text(
                    text = "—${quote.author}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (quote.tags.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    quote.tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            modifier = Modifier
                                .clickable { onTagClick(tag) }
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onFavoriteClick) {
                    Crossfade(targetState = quote.isFavorite) { isFavorite ->
                        val icon =
                            if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                        Icon(
                            imageVector = icon,
                            contentDescription = "Favorite",
                            tint = favoriteColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DismissBackground(
    state: SwipeToDismissBoxState,
    modifier: Modifier = Modifier
) {
    val target = state.targetValue
    val isDeleting = target == SwipeToDismissBoxValue.EndToStart
    val isEditing = target == SwipeToDismissBoxValue.StartToEnd

    val defaultBg = MaterialTheme.colorScheme.surfaceVariant
    val editBg = MaterialTheme.colorScheme.tertiaryContainer
    val deleteBg = MaterialTheme.colorScheme.errorContainer

    val bgColor by animateColorAsState(
        targetValue = when {
            isDeleting -> deleteBg
            isEditing -> editBg
            else -> defaultBg
        },
        animationSpec = tween(250),
        label = "dismissBgColor"
    )

    val editIconAlpha by animateFloatAsState(
        targetValue = if (isEditing) 1f else 0.4f,
        animationSpec = tween(200),
        label = "editIconAlpha"
    )
    val deleteIconAlpha by animateFloatAsState(
        targetValue = if (isDeleting) 1f else 0.4f,
        animationSpec = tween(200),
        label = "deleteIconAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(horizontal = 24.dp)
    ) {
        // Edit (Start -> End)
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = editIconAlpha)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Edit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = editIconAlpha)
            )
        }

        // Delete (End -> Start)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Delete",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = deleteIconAlpha)
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = deleteIconAlpha)
            )
        }
    }
}