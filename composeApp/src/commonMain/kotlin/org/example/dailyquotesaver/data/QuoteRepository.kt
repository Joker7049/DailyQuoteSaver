package org.example.dailyquotesaver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.example.dailyquotesaver.Quote

/**
 * Repository for managing quotes.
 *
 * This class handles the storage and retrieval of quotes using DataStore.
 * It exposes a Flow of quotes that the UI can collect to observe changes.
 *
 * @property prefs The DataStore instance used for storing preferences.
 */
class QuoteRepository(
    private val prefs: DataStore<Preferences>
) {
    /**
     * Key for storing the list of quotes as a JSON string in DataStore.
     */
    private val KEY_QUOTES = stringPreferencesKey("quotes_json")


    /**
     * A Flow that emits the current list of quotes.
     *
     * This Flow automatically emits a new list whenever the underlying data in DataStore changes.
     * The UI can collect this Flow to observe changes and update itself accordingly.
     * If the data in DataStore is missing or cannot be decoded, an empty list is emitted.
     */
    val quotes: Flow<List<Quote>> = prefs.data.map { preferences ->
        val json = preferences[KEY_QUOTES]
        if (json != null) {
            try {
                Json.decodeFromString<List<Quote>>(json)
            } catch (e: Exception) {
                // If decoding fails, return an empty list
                emptyList()
            }
        } else {
            // If there's no data, return an empty list
            emptyList()
        }
    }


    /**
     * Toggles the favorite status of a quote.
     *
     * This function retrieves the current list of quotes, finds the quote with the
     * specified ID, and inverts its `isFavorite` status. The updated list of quotes
     * is then saved back to DataStore.
     *
     * @param quoteId The ID of the quote whose favorite status needs to be toggled.
     */
    suspend fun toggleFavorite(quoteId: Long) {
        prefs.edit { data ->

            val currentQuotes = quotes.first()


            val updatedQuotes = currentQuotes.map { quote ->
                if (quote.id == quoteId) {

                    quote.copy(isFavorite = !quote.isFavorite)
                } else {

                    quote
                }
            }


            data[KEY_QUOTES] = Json.encodeToString(updatedQuotes)
        }
    }


    /**
     * Saves a new quote to the DataStore.
     *
     * This function retrieves the current list of quotes, appends the new quote,
     * and then saves the updated list back to DataStore as a JSON string.
     *
     * @param quote The [Quote] object to be saved.
     */
    suspend fun saveQuote(quote: Quote) {
        prefs.edit { data ->
            val currentQuotes = quotes.first()
            val newQuotes = currentQuotes + quote // Easily add the new quote to the list
            data[KEY_QUOTES] = Json.encodeToString(newQuotes)
        }
    }


    /**
     * Updates an existing quote in the DataStore.
     *
     * This function retrieves the current list of quotes, finds the quote with the
     * same ID as the provided `quote` object, and replaces it with the new `quote` object.
     * The updated list of quotes is then saved back to DataStore.
     *
     * @param quote The [Quote] object containing the updated information.
     */
    suspend fun updateQuote(quote: Quote) {
        prefs.edit { data ->
            val currentQuotes = quotes.first()
            val updateQuotes = currentQuotes.map {
                if (it.id == quote.id) {
                    quote
                } else {
                    it
                }
            }
            data[KEY_QUOTES] = Json.encodeToString(updateQuotes)
        }
    }


    /**
     * Deletes a quote from the DataStore.
     *
     * This function retrieves the current list of quotes, removes the quote with
     * the specified ID, and then saves the updated list back to DataStore.
     *
     * @param quoteId The ID of the quote to be deleted.
     */
    suspend fun deleteQuote(quoteId: Long) {

        prefs.edit { data ->
            val currentQuotes = quotes.first()
            val updatedQuotes = currentQuotes.filter { it.id != quoteId }
            data[KEY_QUOTES] = Json.encodeToString(updatedQuotes)
        }
    }
}