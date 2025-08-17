package org.example.dailyquotesaver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

const val DATA_STORE_FILE_NAME = "dailyquotes.preferences_pb"

// Creates a DataStore instance saving to the given file path
fun createDataStore(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath { producePath().toPath() }
}
