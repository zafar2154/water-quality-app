package com.example.waterquality.ui.screen.homepage

import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.utils.DeviceStatus

data class HomeUiState(
    val currentData: SensorResponse = SensorResponse(null, null, null),
    val phHistory: List<Float> = emptyList(),
    val tdsHistory: List<Float> = emptyList(),
    val tempHistory: List<Float> = emptyList(),
    val turbidityHistory: List<Float> = emptyList(),
    val locationHistory: List<Pair<Double, Double>> = emptyList(),
    val historyList: List<SensorResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** Kapan perangkat terakhir mengirim data live (epoch ms), null jika belum pernah. */
    val lastSeenMillis: Long? = null,
    val deviceStatus: DeviceStatus = DeviceStatus.WAITING,
    /** Koneksi HP ke server Firebase. Default true agar banner tidak berkedip saat start. */
    val isFirebaseConnected: Boolean = true
)
