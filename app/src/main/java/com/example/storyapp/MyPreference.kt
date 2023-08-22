package com.example.storyapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MyPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getLoginState(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_STATE] ?: false
        }
    }

    suspend fun saveLoginState(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_STATE] = isLoggedIn
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[NAME] ?: ""
        }
    }

    suspend fun saveName(name: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }

    companion object {
        @Volatile
        private var instance: MyPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): MyPreference {
            return instance ?: synchronized(this) {
                val newInstance = MyPreference(dataStore)
                instance = newInstance
                newInstance
            }
        }

        private val LOGIN_STATE = booleanPreferencesKey("login_state")
        private val TOKEN = stringPreferencesKey("token")
        private val NAME = stringPreferencesKey("name")
    }
}
