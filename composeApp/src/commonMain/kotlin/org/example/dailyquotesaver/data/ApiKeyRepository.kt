package org.example.dailyquotesaver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class ApiKeyRepository(private val dataStore: DataStore<Preferences>) {

    private val apiKey = stringPreferencesKey("api_key")


    suspend fun getApiKey(): String? {
        return dataStore.data.first()[apiKey]
    }

    suspend fun saveApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[apiKey] = key
        }
    }
}
