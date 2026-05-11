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

// evaluasi status air total
object QualityWaterCheck {
    fun evaluate(ph: Float?, tds: Float?, temp: Float?, turbidity: Float?): WaterStatus {
        if (ph == null || tds == null || temp == null || turbidity == null ||
            ph.isNaN() || tds.isNaN() || temp.isNaN() || turbidity.isNaN()
        ) {
            return WaterStatus.UNKNOWN
        }
        // Standar Kualitas Air
        val isPhSafe = ph in 6.5..8.5
        val isTdsSafe = tds <= 500f
        val isTempSafe = temp in 21f..40f
        val isTurbiditySafe = turbidity < 1f


        return if (isPhSafe && isTdsSafe && isTempSafe && isTurbiditySafe) {
            WaterStatus.SAFE
        } else if (ph !in 5.0..10.0 || tds > 1000f || temp > 60f || turbidity > 5f) {
            WaterStatus.DANGER // Ekstrem
        } else {
            WaterStatus.WARNING // Di tengah-tengah
        }
    }

//  Evaluasi status per sensor
    fun evaluateSensor(type: SensorType, value: Float?): WaterStatus {
        if (value == null || value.isNaN()) return WaterStatus.UNKNOWN
        return when (type) {
            SensorType.PH -> when (value) {
                in 6.5f..8.5f -> WaterStatus.SAFE
                in 5.0f..10.0f -> WaterStatus.WARNING
                else -> WaterStatus.DANGER
            }

            SensorType.TDS -> when {
                value <= 500f -> WaterStatus.SAFE
                value <= 1000f -> WaterStatus.WARNING
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