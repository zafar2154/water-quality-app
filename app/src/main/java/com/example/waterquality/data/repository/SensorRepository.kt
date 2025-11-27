package com.example.waterquality.data.repository

import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.data.remote.ApiService
import com.example.waterquality.storage.IpDataStore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

// Interface agar mudah ditesting
interface SensorRepository {
    fun getSensorData(): Flow<Result<SensorResponse>>
    suspend fun saveToCloud(data: SensorResponse)
    fun getIpAddress(): Flow<String>
    suspend fun saveIpAddress(ip: String)
}