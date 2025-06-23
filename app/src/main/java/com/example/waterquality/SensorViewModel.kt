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


//data class SensorData(val ph: Float, val tds: Float, val temperature: Float)

class SensorViewModel : ViewModel() {
    private val _sensorData = MutableStateFlow(SensorResponse(7.0f, 250f, 25f))
    val sensorData: StateFlow<SensorResponse> = _sensorData
//    private val client = HttpClient(OkHttp) {
//}
//    fun fetchSimulateData() {
//            _sensorData.value = SensorData(
//            ph = Random.nextFloat() * 2f + 6f,
//            tds = Random.nextInt(100, 500).toFloat(),
//            temperature = Random.nextInt(20, 30).toFloat()
//        )
//    }

//    val sensorData = mutableStateOf(SensorResponse(0f, 0f, 0f))
//    fun fetchSensorData() {
//        viewModelScope.launch {
//            try {
//                val response: SensorResponse = client.get("http://192.168.1.2/sensor")
//                sensorData.value = response
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
        fun fetchSensor() {
            viewModelScope.launch {
                while (true) {
                try {
                    val result = ApiClient.retrofit.getUser()
                    Log.d("DEBUG_API", "Hasil: $result")
                    _sensorData.value = result
                } catch (e: Exception) {
                    Log.e("DEBUG_API", "Error: ${e.message}")
                }
                    delay(500)
                }
            }
        }
    }
