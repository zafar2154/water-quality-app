package com.example.waterquality

import ApiClient
import SensorResponse
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random


data class SensorData(val ph: Float, val tds: Float, val temperature: Float)

class SensorViewModel : ViewModel() {
    private val _sensorData = MutableStateFlow(SensorData(7.0f, 250f, 25f))
    val sensorData: StateFlow<SensorData> = _sensorData
//    private val client = HttpClient(OkHttp) {
//}
    fun fetchSimulateData() {
            _sensorData.value = SensorData(
            ph = Random.nextInt(0, 14).toFloat(),
            tds = Random.nextInt(100, 1000).toFloat(),
            temperature = Random.nextInt(0, 70).toFloat()
        )
    }

//        fun fetchSensor() {
//            viewModelScope.launch {
//                while (true) {
//                try {
//                    val result = ApiClient.retrofit.getUser()
//                    Log.d("DEBUG_API", "Hasil: $result")
//                    _sensorData.value = result
//                } catch (e: Exception) {
//                    Log.e("DEBUG_API", "Error: ${e.message}")
//                }
//                    delay(500)
//                }
//            }
//        }
    }
