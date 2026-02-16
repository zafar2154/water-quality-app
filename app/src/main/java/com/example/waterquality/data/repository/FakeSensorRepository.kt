package com.example.waterquality.data.repository

import com.example.waterquality.data.model.SensorResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class FakeSensorRepository @Inject constructor() : SensorRepository {

    override fun getSensorStream(): Flow<SensorResponse> = flow {
        while (true) {
            // 1. Generate angka acak pura-pura
            val dummyData = SensorResponse(
                ph = Random.nextDouble(6.0, 8.0).toFloat(),       // pH antara 6-8
                tds = Random.nextDouble(100.0, 300.0).toFloat(),  // TDS 100-300
                temperature = Random.nextDouble(25.0, 32.0).toFloat() // Suhu 25-32
            )

            // 2. Kirim data ke ViewModel
            emit(dummyData)

            // 3. Tunggu 1 detik sebelum kirim data lagi
            delay(1000)
        }
    }

    override suspend fun saveIp(ip: String) {
        // Tidak melakukan apa-apa karena ini fake
    }
}