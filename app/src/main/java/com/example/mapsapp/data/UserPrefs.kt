package com.example.mapsapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPrefs(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
        val STORE_USERNAME = stringPreferencesKey("store_username")
        val STORE_USERPASS = stringPreferencesKey("store_userpass")
        val STORE_REMEMBERUSER = stringPreferencesKey("store_rememberuser")
    }

    val getUserData: Flow<List<String>> = context.dataStore.data.map { prefs ->
        listOf(
            prefs[STORE_USERNAME] ?: "",
            prefs[STORE_USERPASS] ?: "",
            prefs[STORE_REMEMBERUSER] ?: ""
        )
    }

    suspend fun saveUserData(username: String, userpass: String, rememberuser: String) {
        context.dataStore.edit { prefs ->
            prefs[STORE_USERNAME] = username
            prefs[STORE_USERPASS] = userpass
            prefs[STORE_REMEMBERUSER] = rememberuser
        }
    }
}