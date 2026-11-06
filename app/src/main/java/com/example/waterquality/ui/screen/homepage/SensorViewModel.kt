package com.example.waterquality.ui.screen.homepage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.data.repository.AuthRepository
import com.example.waterquality.data.repository.SensorRepository
import com.example.waterquality.utils.deviceStatusFor
import com.example.waterquality.utils.resolveLastSeen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val repository: SensorRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _sensorData = MutableStateFlow(HomeUiState())
    val sensorData: StateFlow<HomeUiState> = _sensorData.asStateFlow()

    init {
        // Listener Firebase hanya aktif selama ada user yang login.
        // Saat logout (uid -> null), collectLatest membatalkan listener lama
        // sehingga tidak terjadi "permission denied" di luar sesi login.
        viewModelScope.launch {
            authRepository.authState()
                .map { it?.uid }
                .distinctUntilChanged()
                .collectLatest { uid ->
                    _sensorData.value = HomeUiState()
                    if (uid == null) return@collectLatest
                    coroutineScope {
                        launch { observeLiveData() }
                        launch { observeHistoryData() }
                        launch { observeFirebaseConnection() }
                        launch { watchDeviceStatus() }
                    }
                }
        }
    }

    private fun onStreamError(source: String, e: Throwable) {
        Log.e("SensorViewModel", "Stream $source error", e)
        _sensorData.update {
            it.copy(errorMessage = "Gagal memuat $source: ${e.message ?: "kesalahan tidak diketahui"}")
        }
    }

    private suspend fun observeLiveData() {
        repository.getLiveSensorStream()
            .catch { e -> onStreamError("data live", e) }
            .collect { data ->
                // Node kosong (perangkat belum pernah kirim) tidak dihitung sebagai "terlihat"
                val receivedAt = System.currentTimeMillis()
                _sensorData.update { currentState ->
                    val lastSeen = if (data == SensorResponse()) currentState.lastSeenMillis
                    else resolveLastSeen(data.timestamp, receivedAt)

                    val newPhHistory = if (data.ph != null && !data.ph.isNaN()) {
                        (currentState.phHistory + data.ph).takeLast(50)
                    } else currentState.phHistory

                    val newTdsHistory = if (data.tds != null && !data.tds.isNaN()) {
                        (currentState.tdsHistory + data.tds).takeLast(50)
                    } else currentState.tdsHistory

                    val newTempHistory =
                        if (data.temperature != null && !data.temperature.isNaN()) {
                            (currentState.tempHistory + data.temperature).takeLast(50)
                        } else currentState.tempHistory

                    val newTurbidityHistory = if (data.turbidity?.isNaN() == false) {
                        (currentState.turbidityHistory + data.turbidity).takeLast(50)
                    } else currentState.turbidityHistory

                    val newLocationHistory = if (data.lat != null && data.lon != null) {
                        val newPoint = Pair(data.lat, data.lon)
                        // Tambahkan titik hanya jika sensor benar-benar bergerak
                        if (currentState.locationHistory.lastOrNull() != newPoint) {
                            (currentState.locationHistory + newPoint).takeLast(50)
                        } else currentState.locationHistory
                    } else currentState.locationHistory

                    currentState.copy(
                        currentData = data,
                        phHistory = newPhHistory,
                        tdsHistory = newTdsHistory,
                        tempHistory = newTempHistory,
                        turbidityHistory = newTurbidityHistory,
                        locationHistory = newLocationHistory,
                        errorMessage = null,
                        lastSeenMillis = lastSeen,
                        deviceStatus = deviceStatusFor(lastSeen, receivedAt)
                    )
                }
            }
    }

    private suspend fun observeHistoryData() {
        repository.getHistoryStream()
            .catch { e -> onStreamError("riwayat", e) }
            .collect { data ->
                _sensorData.update { state ->
                    val newPoint = if (data.lat != null && data.lon != null)
                        Pair(data.lat, data.lon) else null

                    state.copy(
                        locationHistory =
                            if (newPoint != null && state.locationHistory.lastOrNull() != newPoint)
                                (state.locationHistory + newPoint).takeLast(50)
                            else state.locationHistory,
                        historyList = (state.historyList + data).takeLast(50)
                    )
                }
            }
    }

    private suspend fun observeFirebaseConnection() {
        repository.observeFirebaseConnection()
            .catch { e -> Log.e("SensorViewModel", "Stream koneksi error", e) }
            .collect { connected ->
                _sensorData.update { it.copy(isFirebaseConnected = connected) }
            }
    }

    /**
     * Evaluasi ulang status perangkat secara berkala. Tanpa ini, status tidak
     * pernah berubah jadi OFFLINE karena Firebase tidak mengirim event apa pun
     * ketika perangkat berhenti mengirim data.
     * StateFlow hanya emit jika nilainya berubah, jadi UI tidak recompose tiap detik.
     */
    private suspend fun watchDeviceStatus() {
        while (true) {
            _sensorData.update {
                it.copy(deviceStatus = deviceStatusFor(it.lastSeenMillis, System.currentTimeMillis()))
            }
            delay(STATUS_CHECK_INTERVAL_MS)
        }
    }

    private companion object {
        const val STATUS_CHECK_INTERVAL_MS = 1_000L
    }
}
