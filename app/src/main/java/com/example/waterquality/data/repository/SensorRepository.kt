package com.example.waterquality.data.repository

import android.util.Log
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.data.remote.ApiService
import com.example.waterquality.data.storage.IpDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import javax.inject.Inject

interface SensorRepository {
    fun getSensorStream(): Flow<SensorResponse>
    suspend fun saveIp(ip: String)
}

class SensorRepositoryImpl @Inject constructor(
    private val ipDataStore: IpDataStore,
    private val retrofitBuilder: Retrofit.Builder
) : SensorRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSensorStream(): Flow<SensorResponse> {
        return ipDataStore.getIp().flatMapLatest { ip ->
            flow {
                if (ip.isBlank()) {
                    emit(SensorResponse(null, null, null))
                    return@flow
                }

                // Gunakan try-catch agar tidak crash jika format IP salah
                try {
                    val apiService = createService(ip)
                    while (true) {
                        try {
                            val data = apiService.getUser()
                            emit(data)
                        } catch (e: Exception) {
                            Log.e("SensorRepo", "Fetch error: ${e.message}")
                        }
                        delay(1000)
                    }
                } catch (e: Exception) {
                    Log.e("SensorRepo", "Setup error: ${e.message}")
                }
            }
        }
    }

    override suspend fun saveIp(ip: String) {
        ipDataStore.saveIp(ip)
    }

    private fun createService(ip: String): ApiService {
        val baseUrl = if (ip.startsWith("http")) "$ip/" else "http://$ip/"
        return retrofitBuilder.baseUrl(baseUrl).build().create(ApiService::class.java)
    }
}