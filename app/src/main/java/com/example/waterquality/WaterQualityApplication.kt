package com.example.waterquality

import android.app.Application
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class WaterQualityApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupOsmdroid()
    }

    /**
     * Server tile OpenStreetMap memblokir User-Agent berawalan "com.example"
     * (tile berisi gambar "403 Access blocked", dikirim dengan HTTP 200).
     *
     * Untuk tile source OSM, osmdroid 6.1.x TIDAK memakai userAgentValue, melainkan
     * getNormalizedUserAgent() = "<packageName>/<versionCode>" = "com.example.waterquality/1"
     * (lihat TileDownloader & DefaultConfigurationProvider). Satu-satunya cara menimpanya
     * tanpa mengganti applicationId adalah lewat additionalHttpRequestProperties, yang
     * dipasang SETELAH header User-Agent bawaan sehingga nilainya menang.
     *
     * Jika applicationId nanti diganti (bukan com.example.*), override ini boleh dihapus.
     * Kebijakan: https://operations.osmfoundation.org/policies/tiles/
     */
    private fun setupOsmdroid() {
        val prefs = getSharedPreferences("osmdroid", MODE_PRIVATE)
        val config = Configuration.getInstance().apply {
            load(this@WaterQualityApplication, prefs)
            userAgentValue = OSM_USER_AGENT
            additionalHttpRequestProperties[userAgentHttpHeader] = OSM_USER_AGENT
        }

        // osmdroid menyimpan gambar "Access blocked" di cache seperti tile biasa.
        // Hapus cache sekali agar tile tersebut diunduh ulang dengan User-Agent yang benar.
        if (!prefs.getBoolean(KEY_BLOCKED_TILES_PURGED, false)) {
            val deleted = config.osmdroidTileCache.deleteRecursively()
            Log.i("WaterQualityApp", "Cache tile osmdroid dihapus: $deleted")
            prefs.edit { putBoolean(KEY_BLOCKED_TILES_PURGED, true) }
        }
    }

    private companion object {
        const val OSM_USER_AGENT = "WaterQualityMonitor/1.0 (Android)"

        // Naikkan versi key ini jika cache perlu dibersihkan lagi.
        const val KEY_BLOCKED_TILES_PURGED = "blocked_tiles_purged_v3"
    }
}
