package org.example.dailyquotesaver.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

fun createDataStoreAndroid(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile(DATA_STORE_FILE_NAME)
    }
}
