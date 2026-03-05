package com.example.waterquality.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun TimestampFormat(timestamp: Long?): String {
    if (timestamp == null) return "--:--"
    val date = Date(timestamp * 1000) // Dikali 1000 karena Kotlin pakai milidetik
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(date)
}