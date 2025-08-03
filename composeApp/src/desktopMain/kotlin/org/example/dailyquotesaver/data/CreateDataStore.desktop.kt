package org.example.dailyquotesaver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

/**
 * Creates a DataStore instance for desktop platforms.
 *
 * This function utilizes the `createDataStore` helper function, providing a lambda
 * that resolves the file path for the DataStore on desktop systems.
 * The DataStore file is located in the system's temporary directory, named
 * according to `DATA_STORE_FILE_NAME`.
 *
 * @return A [DataStore] instance of [Preferences] configured for desktop use.
 */
fun createDataStoreDesktop(): DataStore<Preferences> = createDataStore {
    File(System.getProperty("java.io.tmpdir"), DATA_STORE_FILE_NAME).absolutePath
}