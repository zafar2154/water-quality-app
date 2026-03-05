package com.example.waterquality.data.model

import com.google.firebase.Timestamp

data class SensorResponse(
    val id: String? = null,
    val timestamp: Long? = null,
    val ph: Float? = null,
    val tds: Float? = null,
    val temperature: Float? = null,
    val turbidity: Float? = null,
    val lat: Double? = null,
    val lon: Double? = null
) {
    // Constructor kosong otomatis dibuat oleh Kotlin jika semua parameter punya nilai default
}

enum class SensorType {
    PH, TDS, Temperature
}