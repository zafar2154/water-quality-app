package com.example.waterquality.ui.screen.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterquality.data.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val repository: SensorRepository
) : ViewModel() {

    private val _sensorData = MutableStateFlow(HomeUiState())
    val sensorData: StateFlow<HomeUiState> = _sensorData.asStateFlow()

    init {
        observeSensorData()
    }

    private fun observeSensorData() {
        viewModelScope.launch {
            repository.getSensorStream().collect { data ->
                _sensorData.update{ currentState ->
                    val newPhHistory = if (data.ph != null && !data.ph.isNaN()) {
                        (currentState.phHistory + data.ph).takeLast(50)
                    } else currentState.phHistory

                    val newTdsHistory = if (data.tds != null && !data.tds.isNaN()) {
                        (currentState.tdsHistory + data.tds).takeLast(50)
                    } else currentState.tdsHistory

                    val newTempHistory = if (data.temperature != null && !data.temperature.isNaN()) {
                        (currentState.tempHistory + data.temperature).takeLast(50)
                    } else currentState.tempHistory
                    currentState.copy(
                        currentData = data,
                        phHistory = newPhHistory,
                        tdsHistory = newTdsHistory,
                        tempHistory = newTempHistory
                    )
                }
            }
        }
    }

    fun saveIpAddress(ip: String) {
        viewModelScope.launch {
            repository.saveIp(ip)
        }
    }
}