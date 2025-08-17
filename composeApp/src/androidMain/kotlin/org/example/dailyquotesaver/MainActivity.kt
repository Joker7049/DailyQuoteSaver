package org.example.dailyquotesaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import org.example.dailyquotesaver.data.ApiKeyRepository
import org.example.dailyquotesaver.data.QuoteRepository
import org.example.dailyquotesaver.data.createDataStore
import org.example.dailyquotesaver.data.createDataStoreAndroid


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val dataStore = createDataStoreAndroid(applicationContext)
        val quoteRepository = QuoteRepository(dataStore)
        val apiKeyRepository = ApiKeyRepository(dataStore)


        setContent {
            App(quoteRepository, apiKeyRepository)
        }
    }
}

