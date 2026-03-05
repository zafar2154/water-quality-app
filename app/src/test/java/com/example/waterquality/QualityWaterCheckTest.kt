package com.example.waterquality

import com.example.waterquality.data.model.SensorType
import com.example.waterquality.utils.QualityWaterCheck
import com.example.waterquality.utils.WaterStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class WaterQualityCheckTest {

    @Test
    fun `evaluasi global harus SAFE jika semua parameter normal`() {
        val status = QualityWaterCheck.evaluate(ph = 7.0f, tds = 100f, temp = 25f)
        assertEquals(WaterStatus.SAFE, status)
    }

    @Test
    fun `evaluasi global harus DANGER jika ph terlalu rendah`() {
        val status = QualityWaterCheck.evaluate(ph = 3.0f, tds = 100f, temp = 25f)
        assertEquals(WaterStatus.DANGER, status)
    }

    @Test
    fun `evaluasi global harus OFFLINE jika ada data null atau NaN`() {
        val statusNull = QualityWaterCheck.evaluate(ph = null, tds = 100f, temp = null)
        val statusNaN = QualityWaterCheck.evaluate(ph = Float.NaN, tds = 100f, temp = Float.NaN)

        assertEquals(WaterStatus.UNKNOWN, statusNull)
        assertEquals(WaterStatus.UNKNOWN, statusNaN)
    }

    @Test
    fun `evaluasi sensor individual TDS harus SAFE di bawah 500`() {
        val status = QualityWaterCheck.evaluateSensor(SensorType.TDS, 150f)
        assertEquals(WaterStatus.SAFE, status)
    }

    @Test
    fun `evaluasi sensor individual TDS harus WARNING antara 500 sampai 1000`() {
        val status = QualityWaterCheck.evaluateSensor(SensorType.TDS, 750f)
        assertEquals(WaterStatus.WARNING, status)
    }

    @Test
    fun `evaluasi sensor individual TDS harus DANGER di atas 1000`() {
        val status = QualityWaterCheck.evaluateSensor(SensorType.TDS, 1200f)
        assertEquals(WaterStatus.DANGER, status)
    }
}