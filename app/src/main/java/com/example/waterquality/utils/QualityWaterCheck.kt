package com.example.waterquality.utils
import androidx.compose.ui.graphics.Color
import com.example.waterquality.data.model.SensorType

// Enum untuk menyeragamkan Status di seluruh aplikasi
enum class WaterStatus(val label: String, val composeColor: Color, val androidColor: Int) {
    SAFE("Aman (Layak)", Color(0xFF4CAF50), android.graphics.Color.GREEN),
    WARNING("Waspada", Color(0xFFFFC107), android.graphics.Color.YELLOW),
    DANGER("Berbahaya", Color(0xFFF44336), android.graphics.Color.RED),
    UNKNOWN("Menunggu Data", Color.Gray, android.graphics.Color.GRAY)
}

object QualityWaterCheck {
    fun evaluate(ph: Float?, tds: Float?, temp: Float?): WaterStatus {
        if (ph == null || tds == null || temp == null ||
            ph.isNaN() || tds.isNaN() || temp.isNaN()) {
            return WaterStatus.UNKNOWN
        }
        // Masukkan standar kualitas air di sini (Bisa disesuaikan dengan teori skripsi Anda)
        val isPhSafe = ph in 6.5..8.5
        val isTdsSafe = tds <= 500f
        val isTurbiditySafe = temp in 21f..40f

        return if (isPhSafe && isTdsSafe && isTurbiditySafe) {
            WaterStatus.SAFE
        } else if (ph !in 5.0..10.0 || tds > 1000f || temp > 60f) {
            WaterStatus.DANGER // Ekstrem
        } else {
            WaterStatus.WARNING // Di tengah-tengah
        }
    }

    fun evaluateSensor(type: SensorType, value: Float?): WaterStatus {
        if (value == null || value.isNaN()) return WaterStatus.UNKNOWN

        return when (type) {
            SensorType.PH -> {
                if (value in 6.5f..8.5f) WaterStatus.SAFE else WaterStatus.DANGER
            }
            SensorType.TDS -> {
                if (value <= 500f) WaterStatus.SAFE
                else if (value <= 1000f) WaterStatus.WARNING
                else WaterStatus.DANGER
            }
            SensorType.Temperature -> {
                // Logika suhu lama kamu
                if (value in 21f..40f) WaterStatus.SAFE else WaterStatus.WARNING
            }
        }
    }
}