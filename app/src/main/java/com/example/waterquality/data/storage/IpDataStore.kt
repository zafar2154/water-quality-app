package com.example.waterquality.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object IpDataStore {
    private val IP_KEY = stringPreferencesKey("ip_address")

    fun getIp(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[IP_KEY] ?: ""
        }
    }

    suspend fun saveIp(context: Context, ip: String) {
        context.dataStore.edit { preferences ->
            preferences[IP_KEY] = ip
        }
    }
}
