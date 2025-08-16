package org.example.dailyquotesaver


import androidx.compose.ui.window.ComposeUIViewController
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.example.dailyquotesaver.data.QuoteRepository
import org.example.dailyquotesaver.data.createDataStore
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIViewController



fun MainScreenViewController(): UIViewController {
    val repository = createQuoteRepository()
    return ComposeUIViewController { App(repo = repository) }
}

// Helper function to create DataStore<Preferences>
private fun createActualDataStore(): DataStore<Preferences> {
    val documentsDirectory = NSSearchPathForDirectoriesInDomains(
        directory = NSDocumentDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true
    ).first() as String
    val dataStoreFileName = "daily_quote_saver.preferences_pb"
    val fullPath = "$documentsDirectory/$dataStoreFileName"
    return createDataStore { fullPath } // createDataStore is your actual factory from nativeMain
}

private fun createQuoteRepository(): QuoteRepository {
    val dataStore = createActualDataStore() // Get the DataStore instance
    return QuoteRepository(prefs = dataStore) // Pass it to the repository
}

