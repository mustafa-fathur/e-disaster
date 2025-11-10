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

// This creates a singleton instance of DataStore for our app, named "user_prefs".
// The 'by preferencesDataStore' delegate ensures we have only one instance.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton // Informs Hilt that there should only be one instance of this class for the whole app.
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    // 1. Define a key for our auth token. This is a typesafe key for a String.
    private val authTokenKey = stringPreferencesKey("auth_token")

    // 2. Create a public Flow to read the token.
    // The UI and other parts of the app can observe this Flow.
    // When the token changes in DataStore, any collector of this Flow will get the new value automatically.
    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[authTokenKey]
        }

    // 3. A suspend function to save the token.
    // We use `edit` to make changes in a safe, transactional way.
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[authTokenKey] = token
        }
    }

    // 4. A suspend function to clear the token (used for logout).
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(authTokenKey)
        }
    }
}
