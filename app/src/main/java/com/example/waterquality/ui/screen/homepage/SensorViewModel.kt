package com.example.waterquality.ui.screen.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.data.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject // <--- PENTING: Pakai javax, JANGAN jakarta

@HiltViewModel // <--- PENTING: Wajib ada
class SensorViewModel @Inject constructor(
    private val repository: SensorRepository
) : ViewModel() {

    private val _sensorData = MutableStateFlow(SensorResponse(null, null, null))
    val sensorData: StateFlow<SensorResponse> = _sensorData.asStateFlow()

    init {
        observeSensorData()
    }

    private fun observeSensorData() {
        viewModelScope.launch {
            repository.getSensorStream().collect { data ->
                _sensorData.value = data
            }
        }
    }

    fun saveIpAddress(ip: String) {
        viewModelScope.launch {
            repository.saveIp(ip)
        }
    }
}