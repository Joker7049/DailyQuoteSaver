package org.example.dailyquotesaver

import androidx.compose.ui.window.ComposeUIViewController
import org.example.dailyquotesaver.data.ApiKeyRepository
import org.example.dailyquotesaver.data.QuoteRepository
import org.example.dailyquotesaver.data.createDataStoreNative

fun MainViewController() = ComposeUIViewController {
    val dataStore = createDataStoreNative()
    val quoteRepository = QuoteRepository(dataStore)
    val apiKeyRepository = ApiKeyRepository(dataStore)
    App(quoteRepository, apiKeyRepository)
}