package com.example.e_disaster.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val authTokenKey = stringPreferencesKey("auth_token")
    private val fcmTokenKey = stringPreferencesKey("fcm_token")

    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[authTokenKey]
        }


    val fcmToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[fcmTokenKey]
        }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[authTokenKey] = token
        }
    }

    suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[fcmTokenKey] = token
        }
    }

    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(authTokenKey)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
