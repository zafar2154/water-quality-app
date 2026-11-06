package com.example.waterquality

import com.example.waterquality.utils.DEVICE_OFFLINE_THRESHOLD_MS
import com.example.waterquality.utils.DeviceStatus
import com.example.waterquality.utils.deviceStatusFor
import com.example.waterquality.utils.formatElapsed
import com.example.waterquality.utils.resolveLastSeen
import com.example.waterquality.utils.timestampToMillis
import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceStatusTest {

    // 2026-11-06 00:00:00 UTC
    private val now = 1_793_923_200_000L

    @Test
    fun `timestamp detik dikonversi ke milidetik`() {
        assertEquals(now, timestampToMillis(now / 1000))
    }

    @Test
    fun `timestamp milidetik tidak diubah`() {
        assertEquals(now, timestampToMillis(now))
    }

    @Test
    fun `timestamp perangkat valid dipakai sebagai lastSeen`() {
        val deviceTs = now - 60_000
        assertEquals(deviceTs, resolveLastSeen(deviceTs, receivedAtMillis = now))
    }

    @Test
    fun `timestamp perangkat valid dalam detik juga diterima`() {
        val deviceTs = now - 60_000
        assertEquals(deviceTs, resolveLastSeen(deviceTs / 1000, receivedAtMillis = now))
    }

    @Test
    fun `timestamp kosong fallback ke waktu diterima`() {
        assertEquals(now, resolveLastSeen(null, receivedAtMillis = now))
    }

    @Test
    fun `timestamp millis sejak boot (NTP belum sinkron) fallback ke waktu diterima`() {
        assertEquals(now, resolveLastSeen(123_456L, receivedAtMillis = now))
    }

    @Test
    fun `timestamp jauh di masa depan fallback ke waktu diterima`() {
        assertEquals(now, resolveLastSeen(now + 3_600_000, receivedAtMillis = now))
    }

    @Test
    fun `jam perangkat sedikit lebih cepat dipotong ke waktu diterima`() {
        assertEquals(now, resolveLastSeen(now + 10_000, receivedAtMillis = now))
    }

    @Test
    fun `belum ada data berarti WAITING`() {
        assertEquals(DeviceStatus.WAITING, deviceStatusFor(null, now))
    }

    @Test
    fun `data baru berarti ONLINE`() {
        assertEquals(DeviceStatus.ONLINE, deviceStatusFor(now - 5_000, now))
    }

    @Test
    fun `tepat di batas threshold masih ONLINE`() {
        assertEquals(DeviceStatus.ONLINE, deviceStatusFor(now - DEVICE_OFFLINE_THRESHOLD_MS, now))
    }

    @Test
    fun `lewat threshold berarti OFFLINE`() {
        assertEquals(DeviceStatus.OFFLINE, deviceStatusFor(now - DEVICE_OFFLINE_THRESHOLD_MS - 1, now))
    }

    @Test
    fun `format waktu relatif`() {
        assertEquals("12 detik lalu", formatElapsed(12_000))
        assertEquals("5 menit lalu", formatElapsed(5 * 60_000L))
        assertEquals("2 jam lalu", formatElapsed(2 * 3_600_000L))
        assertEquals("3 hari lalu", formatElapsed(3 * 86_400_000L))
        assertEquals("0 detik lalu", formatElapsed(-500))
    }
}
