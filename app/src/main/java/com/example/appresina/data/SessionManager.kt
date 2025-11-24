package com.example.appresina.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    // Flujo reactivo que indica si el usuario está autenticado
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    // Flujo reactivo con el ID del usuario actual
    val userId: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    // Flujo reactivo con el email del usuario actual
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    // Flujo reactivo con el nombre del usuario actual
    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    // Inicia sesión guardando los datos del usuario
    suspend fun login(userId: Int, email: String, userName: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = userName
        }
    }

    // Cierra sesión eliminando los datos del usuario
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(USER_ID)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_NAME)
        }
    }

    // Obtiene el ID del usuario actual de forma síncrona (no recomendado, usar el Flow)
    suspend fun getCurrentUserId(): Int? {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }.first()
    }
}

