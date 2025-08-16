package org.example.dailyquotesaver

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.dailyquotesaver.data.QuoteRepository
import org.example.dailyquotesaver.network.GeminiQuoteService
import org.example.dailyquotesaver.ui.AddOrGenerateScreen
import org.example.dailyquotesaver.ui.AddQuoteFab
import org.example.dailyquotesaver.ui.EditQuoteScreen
import org.example.dailyquotesaver.ui.ElegantBottomBar
import org.example.dailyquotesaver.ui.FavoritesScreen
import org.example.dailyquotesaver.ui.GenerateUiState
import org.example.dailyquotesaver.ui.HomeScreen
import org.example.dailyquotesaver.ui.QuoteScreen
import org.example.dailyquotesaver.ui.QuoteTopBar
import org.example.dailyquotesaver.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class Screen { QUOTE, FAVORITES, Edit, ADD_OR_GENERATE, Home }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(repo: QuoteRepository) {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    // ... (the rest of your state variables are unchanged)
    val allQuotes by repo.quotes.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    val aiService = remember { GeminiQuoteService() }
    var generateUiState by remember { mutableStateOf<GenerateUiState>(GenerateUiState.Idle) }
    var searchQuery by remember { mutableStateOf("") }
    var activeTagFilter by remember { mutableStateOf<String?>((null)) }
    var editedQuote by remember { mutableStateOf<Quote?>(null) }
    var quoteToDelete by remember { mutableStateOf<Quote?>(null) }
    val filteredQuotes by remember(allQuotes, searchQuery, activeTagFilter) {
        mutableStateOf(
            allQuotes
                .filter {
                    searchQuery.isBlank() ||
                            it.text.contains(searchQuery.trim(), ignoreCase = true) ||
                            it.author?.contains(searchQuery.trim(), ignoreCase = true) == true
                }
                .filter {
                    activeTagFilter == null || it.tags.contains(activeTagFilter)
                }
        )
    }
    var randomQuoteTrigger by remember { mutableStateOf(0) }
    val randomQuote = remember(allQuotes, randomQuoteTrigger) { allQuotes.randomOrNull() }

    AppTheme {
        Scaffold(
            topBar = {
                if (currentScreen == Screen.QUOTE) {
                    QuoteTopBar(onGoToFavorites = { currentScreen = Screen.FAVORITES })
                }
                else if (currentScreen == Screen.FAVORITES) {
                    TopAppBar(
                        title = { Text("Favorite Quotes") },
                        navigationIcon = {
                            IconButton(onClick = {currentScreen = Screen.QUOTE}) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }else if (currentScreen == Screen.Edit) {
                    TopAppBar(
                        title = { Text("Edit Quote") },
                        navigationIcon = {
                            IconButton(onClick = {currentScreen = Screen.QUOTE}) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }else if (currentScreen == Screen.ADD_OR_GENERATE) {
                    TopAppBar(
                        title = { Text("Add a New Quote") },
                        navigationIcon = {
                            IconButton(onClick = {currentScreen = Screen.QUOTE}) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
                // else if (currentScreen == Screen.ANOTHER_SCREEN) { AnotherTopBar() }
            },
            bottomBar = {
                if (currentScreen == Screen.Home || currentScreen == Screen.QUOTE) {
                    val navItems = listOf(
                        BottomNavItem(label = "Home", screen = Screen.Home, icon = Icons.Default.Home),
                        BottomNavItem(label = "Quotes", screen = Screen.QUOTE, icon = Icons.Default.FormatQuote)
                    )
                    ElegantBottomBar(
                        navItems = navItems,
                        currentScreen = currentScreen,
                        onTabSelected = { screen -> currentScreen = screen }
                    )
                }
            },
            floatingActionButton = {
                if (currentScreen == Screen.QUOTE) {
                    AddQuoteFab(onAddClick = { currentScreen = Screen.ADD_OR_GENERATE })
                }
                // You can add other FABs for other screens here if needed
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                // ... (Your AlertDialog and when(currentScreen) block are unchanged)
                quoteToDelete?.let { quote ->
                    AlertDialog(
                        onDismissRequest = { quoteToDelete = null },
                        title = { Text("Confirm Deletion") },
                        text = { Text("Are you sure you want to delete this quote:${quote.text}?") },
                        confirmButton = {
                            Button(onClick = {
                                scope.launch {
                                    repo.deleteQuote(quote.id)
                                    quoteToDelete = null
                                }
                            }) { Text("Delete") }
                        },
                        dismissButton = {
                            Button(onClick = { quoteToDelete = null }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                when (currentScreen) {
                    Screen.QUOTE -> QuoteScreen(
                        repository = repo,
                        quotes = filteredQuotes,
                        onDeleteRequest = { id -> quoteToDelete = allQuotes.find { it.id == id } },
                        searchQuery = searchQuery,
                        activeTagFilter = activeTagFilter,
                        onTagClick = { tag -> activeTagFilter = tag },
                        onClearTagFilter = { activeTagFilter = null },
                        onSearchQueryChange = { searchQuery = it },
                        onEditClick = { quote -> editedQuote = quote; currentScreen = Screen.Edit },
                    )

                    Screen.FAVORITES -> FavoritesScreen(
                        favoriteQuotes = allQuotes.filter { it.isFavorite },
                        onFavoriteClick = { scope.launch { repo.toggleFavorite(it) } },
                        onDeleteRequest = { id -> quoteToDelete = allQuotes.find { it.id == id } },
                        onTagClick = { tag -> activeTagFilter = tag },
                        onEditClick = { quote -> editedQuote = quote; currentScreen = Screen.Edit }
                    )

                    Screen.Edit -> editedQuote?.let { quote ->
                        EditQuoteScreen(
                            quoteToEdit = quote,
                            onSaveClick = {
                                scope.launch {
                                    repo.updateQuote(it)
                                    currentScreen = Screen.QUOTE
                                }
                            },
                        )
                    } ?: run { currentScreen = Screen.QUOTE }

                    Screen.ADD_OR_GENERATE -> AddOrGenerateScreen(
                        generateUiState = generateUiState,
                        onGenerateQuote = { prompt ->
                            scope.launch {
                                generateUiState = GenerateUiState.Loading
                                try {
                                    val result = aiService.generateQuote(prompt)
                                    generateUiState = GenerateUiState.Success(result)
                                } catch (e: Exception) {
                                    println("AI Generation Failed: ${e.message}")
                                    generateUiState =
                                        GenerateUiState.Error("Sorry, something went wrong. Please try again.")
                                }
                            }
                        },
                        onSaveQuote = { text, author, tags ->
                            scope.launch {
                                val tagsList = tags.split(',')
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }
                                repo.saveQuote(
                                    Quote(
                                        text = text,
                                        author = author.ifBlank { null },
                                        tags = tagsList
                                    )
                                )
                                currentScreen = Screen.QUOTE
                            }
                        },
                        resetGenerateUiState = { generateUiState = GenerateUiState.Idle }
                    )

                    Screen.Home -> HomeScreen(
                        quote = randomQuote,
                        onRefresh = { randomQuoteTrigger++ }
                    )
                }
            }
        }
    }
}

// ===================================================================
// ==  ADAPTED "CREATIVE WAVE" COMPOSABLES
// ===================================================================

/**
 * The custom shape for the bottom bar.
 */
class WaveBottomBarShape(private val dip: Float = 0.15f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            moveTo(x = size.width, y = 0f)
            quadraticTo(
                x1 = size.width * 0.5f,
                y1 = size.height * dip * 2,
                x2 = 0f,
                y2 = 0f
            )
            close()
        }
        return Outline.Generic(path)
    }
}

// Data class for navigation items (same as before)
data class BottomNavItem(
    val label: String,
    val screen: Screen,
    val icon: ImageVector
)

/**
 * The creative and stylized wave bottom bar.
 */
@Composable
fun CreativeWaveBottomBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        BottomNavItem(label = "Home", screen = Screen.Home, icon = Icons.Default.Home),
        BottomNavItem(label = "Quotes", screen = Screen.QUOTE, icon = Icons.Default.FormatQuote)
    )

    Box(
        modifier = modifier
            .shadow(elevation = 10.dp, shape = WaveBottomBarShape())
            .graphicsLayer {
                shape = WaveBottomBarShape()
                clip = true
            }
            .background(
                Brush.verticalGradient(
                    if (currentScreen == Screen.Home){
                        listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp).copy(alpha = 0.1f)
                        )
                    } else {
                         listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        )
                    }
                )
            )
            .height(64.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = item.screen == currentScreen
                val iconSize by animateDpAsState(targetValue = if (isSelected) 32.dp else 24.dp)

                IconButton(onClick = { onTabSelected(item.screen) }) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(iconSize),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
