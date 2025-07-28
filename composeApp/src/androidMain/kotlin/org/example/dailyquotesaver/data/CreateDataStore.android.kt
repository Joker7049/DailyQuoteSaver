package org.example.dailyquotesaver.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Creates a DataStore instance for Android.
 *
 * This function utilizes the application context to determine the file path for the DataStore.
 * The DataStore will be created in the application's internal files directory.
 *
 * @param context The application context.
 * @return A DataStore instance for Preferences.
 */
fun createDataStoreAndroid(context: Context): DataStore<Preferences> = createDataStore {
    context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
}