package com.example.waterquality

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

data class SensorData(val ph: Float, val tds: Float, val temperature: Float)

class SensorViewModel : ViewModel() {
    private val _sensorData = MutableStateFlow(SensorData(7.0f, 250f, 25f))
    val sensorData: StateFlow<SensorData> = _sensorData

    fun simulateData() {
        _sensorData.value = SensorData(
            ph = Random.nextFloat() * 2f + 6f,
            tds = Random.nextInt(100, 500).toFloat(),
            temperature = Random.nextInt(20, 30).toFloat()
        )
    }
}
