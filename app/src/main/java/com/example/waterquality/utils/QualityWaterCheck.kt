package com.example.waterquality.utils
import androidx.compose.ui.graphics.Color
import com.example.waterquality.data.model.SensorType

// Enum untuk menyeragamkan Status di seluruh aplikasi
enum class WaterStatus(val label: String, val composeColor: Color, val androidColor: Int) {
    SAFE("Aman (Layak)", Color(0xFF4CAF50), android.graphics.Color.GREEN),
    WARNING("Waspada", Color(0xFFFFC107), android.graphics.Color.YELLOW),
    DANGER("Tidak Layak", Color(0xFFF44336), android.graphics.Color.RED),
    UNKNOWN("Menunggu Data", Color.Gray, android.graphics.Color.GRAY);

    companion object {
        fun fromKategori(kategori: String?): WaterStatus {
            if (kategori == null) return UNKNOWN
            return when (kategori.trim().lowercase()) {
                "layak", "aman", "aman (layak)" -> SAFE
                "waspada" -> WARNING
                "tidak layak", "berbahaya" -> DANGER
                else -> UNKNOWN
            }
        }
    }
}

object QualityWaterCheck {
    fun evaluateSensor(type: SensorType, value: Float?): WaterStatus {
        if (value == null || value.isNaN()) return WaterStatus.UNKNOWN
        return when (type) {
            SensorType.PH -> when (value) {
                in 6.5f..8.5f -> WaterStatus.SAFE
                in 6.0f..9.0f -> WaterStatus.WARNING
                else -> WaterStatus.DANGER
            }

            SensorType.TDS -> when {
                value <= 300f -> WaterStatus.SAFE
                value <= 600f -> WaterStatus.WARNING
                else -> WaterStatus.DANGER
            }

            SensorType.Temperature -> when {
                value in 21f..40f -> WaterStatus.SAFE
                else -> WaterStatus.WARNING
            }
            SensorType.Turbidity -> when {
                value < 1f -> WaterStatus.SAFE
                value <= 5f -> WaterStatus.WARNING
                else -> WaterStatus.DANGER
            }
        }
    }
}