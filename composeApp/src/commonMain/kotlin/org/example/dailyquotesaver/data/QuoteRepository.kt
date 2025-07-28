package org.example.dailyquotesaver.data




import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.dailyquotesaver.Quote


class QuoteRepository(
    private val prefs: DataStore<Preferences>
) {
    private val KEY_QUOTES = stringPreferencesKey("quotes_json")

    suspend fun getQuotes(): List<Quote> {
        val prefsData = prefs.data.first()
        val json = prefsData[KEY_QUOTES] ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveQuote(quote: Quote) {
        val current = getQuotes().toMutableList()
        current.add(quote)
        val json = Json.encodeToString(current)
        prefs.edit { data ->
            data[KEY_QUOTES] = json
        }
    }
}
