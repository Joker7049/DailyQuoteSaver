package org.example.dailyquotesaver

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.dailyquotesaver.data.QuoteRepository
import org.example.dailyquotesaver.data.createDataStoreDesktop
import org.example.dailyquotesaver.ui.QuoteScreen

fun main() {

    application {
        val prefs = createDataStoreDesktop()
        val repository = QuoteRepository(prefs)

        Window(
            onCloseRequest = ::exitApplication,
            title = "DailyQuoteSaver",
        ) {

            QuoteScreen(repository)
        }
    }
}