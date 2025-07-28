package org.example.dailyquotesaver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDataStoreDesktop(): DataStore<Preferences> = createDataStore {
    File(System.getProperty("java.io.tmpdir"), DATA_STORE_FILE_NAME).absolutePath
}