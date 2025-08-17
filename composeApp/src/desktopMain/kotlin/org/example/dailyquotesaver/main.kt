package org.example.dailyquotesaver

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.dailyquotesaver.data.ApiKeyRepository
import org.example.dailyquotesaver.data.QuoteRepository
import org.example.dailyquotesaver.data.createDataStoreDesktop

fun main() {

    application {
        val dataStore = createDataStoreDesktop()
        val quoteRepository = QuoteRepository(dataStore)
        val apiKeyRepository = ApiKeyRepository(dataStore)

        Window(
            onCloseRequest = ::exitApplication,
            title = "DailyQuoteSaver",
        ) {

            App(quoteRepository, apiKeyRepository)
        }
    }
}