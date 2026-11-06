package com.example.waterquality.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Batas pembeda detik vs milidetik. 1e11 detik = tahun 5138,
 * 1e11 milidetik = tahun 1973 -> aman untuk membedakan keduanya.
 */
private const val SECONDS_MILLIS_BOUNDARY = 100_000_000_000L

/**
 * Normalisasi timestamp perangkat ke epoch milidetik.
 * Mendukung epoch detik (time() dari NTP) maupun milidetik
 * (ServerValue.TIMESTAMP / {".sv":"timestamp"}).
 */
fun timestampToMillis(timestamp: Long): Long =
    if (timestamp < SECONDS_MILLIS_BOUNDARY) timestamp * 1000 else timestamp

fun TimestampFormat(timestamp: Long?): String {
    if (timestamp == null) return "--:--"
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestampToMillis(timestamp)))
}

/** Contoh: "12 detik lalu", "5 menit lalu", "2 jam lalu", "3 hari lalu". */
fun formatElapsed(elapsedMs: Long): String {
    val seconds = (elapsedMs / 1000).coerceAtLeast(0)
    return when {
        seconds < 60 -> "$seconds detik lalu"
        seconds < 3_600 -> "${seconds / 60} menit lalu"
        seconds < 86_400 -> "${seconds / 3_600} jam lalu"
        else -> "${seconds / 86_400} hari lalu"
    }
}
