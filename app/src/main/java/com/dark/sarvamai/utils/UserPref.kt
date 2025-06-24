package com.dark.sarvamai.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "settings")

object UserPrefs {

    private val API_KEY = stringPreferencesKey("api_key")

    // Use this in a suspend function to get the API key directly as String
    suspend fun getApiKey(context: Context): String {
        return context.dataStore.data.first()[API_KEY] ?: "none"
    }

    suspend fun setApiKey(context: Context, apiKey: String) {
        context.dataStore.edit { it[API_KEY] = apiKey }
    }
}
