package com.example.waterquality.data.remote

import com.example.waterquality.data.model.SensorResponse
import retrofit2.http.GET

interface ApiService {
    @GET("sensor")
    suspend fun getUser(): SensorResponse
}