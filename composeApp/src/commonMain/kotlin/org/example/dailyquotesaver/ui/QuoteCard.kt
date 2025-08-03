

package org.example.dailyquotesaver.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.dailyquotesaver.Quote

@Composable
fun QuoteCard(
    quote: Quote,
    onFavoriteClick: (Long) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    // The new parameter with a default value.
    searchQuery: String = "",
    onEditClick: (Quote) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column for the text, taking up most of the space
            Column(modifier = Modifier.weight(1f)) {
                // The Text composable using our highlighting function
                Text(
                    text = buildHighlightedText(text = quote.text, query = searchQuery),
                    style = MaterialTheme.typography.bodyLarge
                )

                // The author text, also using our highlighting function
                if (!quote.author.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildHighlightedText(text = quote.author, query = searchQuery),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (quote.tags.isNotEmpty()){
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ){
                        quote.tags.forEach { tag ->
                            Text(
                                modifier = Modifier
                                    .clickable{
                                        onTagClick(tag)
                                    }
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = "#$tag",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            // The IconButton for favoriting
            IconButton(onClick = { onFavoriteClick(quote.id) }) {
                Icon(
                    imageVector = if (quote.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = "Toggle Favorite",
                    tint = if (quote.isFavorite) Color.Red else Color.Gray
                )
            }

            IconButton(onClick = {onDeleteRequest(quote.id)}){
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "delete the quote",
                )
            }

            IconButton(onClick = {onEditClick(quote)}){
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "update the quote",
                )
            }
        }
    }
}