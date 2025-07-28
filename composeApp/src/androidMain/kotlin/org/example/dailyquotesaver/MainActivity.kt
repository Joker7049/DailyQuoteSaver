package org.example.dailyquotesaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import org.example.dailyquotesaver.data.QuoteRepository

import org.example.dailyquotesaver.data.createDataStoreAndroid
import org.example.dailyquotesaver.ui.QuoteScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val prefs = createDataStoreAndroid(applicationContext)
        val repository = QuoteRepository(prefs)


        setContent {
            QuoteScreen(repository)
        }
    }
}

