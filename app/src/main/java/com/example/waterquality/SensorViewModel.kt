package com.example.waterquality

import ApiClient
import ApiService
import SensorResponse
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterquality.storage.IpDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random


data class SensorData(val ph: Float?, val tds: Float?, val temperature: Float?)

class SensorViewModel : ViewModel() {
    private val _sensorData = MutableStateFlow(SensorResponse(null, null, null))
    val sensorData: StateFlow<SensorResponse> = _sensorData

    private var apiService: ApiService? = null

    fun initApi(ipAddress: String) {
        apiService = ApiClient.create(ipAddress)
    }

    fun loadIpAndStart(context: Context) {
        viewModelScope.launch {
            IpDataStore.getIp(context).collect { ip ->
                if (ip.isNotBlank()) {
                    initApi(ip)
                    fetchSensor()
                }
            }
        }
    }

    fun fetchSensor() {
        viewModelScope.launch {
            while (true) {
                try {
                    val result = apiService?.getUser()
                    Log.d("DEBUG_API", "Hasil: $result")
                    result?.let { _sensorData.value = it }
                } catch (e: Exception) {
                    Log.e("DEBUG_API", "Error: ${e.message}")
                }
                delay(500)
            }
        }
    }
    }
