package com.example.waterquality.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Penting: Pastikan variabel context.dataStore ditaruh di luar class (Top Level)
private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class IpDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val IP_KEY = stringPreferencesKey("ip_address")

    fun getIp(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[IP_KEY] ?: ""
        }
    }

    suspend fun saveIp(ip: String) {
        context.dataStore.edit { preferences ->
            preferences[IP_KEY] = ip
        }
    }
}