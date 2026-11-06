package com.example.waterquality.utils

/** Status perangkat sensor berdasarkan kapan data live terakhir diterima. */
enum class DeviceStatus {
    /** Belum pernah ada data live sejak app dibuka / login. */
    WAITING,
    ONLINE,
    OFFLINE
}

/**
 * Perangkat melakukan set() ke live_status setiap 5 detik.
 * 3x interval -> toleransi 2 paket hilang sebelum dianggap offline.
 * Sesuaikan jika interval kirim di firmware diubah.
 */
const val DEVICE_OFFLINE_THRESHOLD_MS = 15_000L

/** Timestamp sebelum 2024-01-01 dianggap tidak valid (jam perangkat belum sinkron NTP). */
private const val MIN_VALID_TIMESTAMP_MS = 1_704_067_200_000L

/** Toleransi jam perangkat lebih cepat daripada jam HP. */
private const val MAX_FUTURE_SKEW_MS = 60_000L

/**
 * Menentukan kapan perangkat terakhir terlihat (epoch ms).
 *
 * - Jika timestamp dari perangkat valid -> pakai itu. Ini penting supaya data lama
 *   yang langsung dikirim Firebase saat app dibuka TIDAK dianggap "online".
 * - Jika timestamp kosong / tidak masuk akal (NTP belum sinkron, millis() sejak boot,
 *   jauh di masa depan) -> fallback ke waktu data diterima HP.
 */
fun resolveLastSeen(deviceTimestamp: Long?, receivedAtMillis: Long): Long {
    val deviceMillis = deviceTimestamp?.let(::timestampToMillis) ?: return receivedAtMillis
    val isValid = deviceMillis >= MIN_VALID_TIMESTAMP_MS &&
        deviceMillis <= receivedAtMillis + MAX_FUTURE_SKEW_MS
    return if (isValid) minOf(deviceMillis, receivedAtMillis) else receivedAtMillis
}

fun deviceStatusFor(
    lastSeenMillis: Long?,
    nowMillis: Long,
    thresholdMs: Long = DEVICE_OFFLINE_THRESHOLD_MS
): DeviceStatus = when {
    lastSeenMillis == null -> DeviceStatus.WAITING
    nowMillis - lastSeenMillis > thresholdMs -> DeviceStatus.OFFLINE
    else -> DeviceStatus.ONLINE
}
