package com.example.waterquality.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.waterquality.data.model.SensorResponse
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportService {

    /**
     * Ekspor list SensorResponse ke file CSV di folder Downloads.
     * Return: nama file jika berhasil, null jika gagal.
     */
    fun exportToCsv(context: Context, data: List<SensorResponse>): String? {
        if (data.isEmpty()) return null

        val fileName = "water_quality_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        }.csv"

        return try {
            val outputStream = openOutputStream(context, fileName) ?: return null
            outputStream.use { stream ->
                stream.write(buildCsvContent(data).toByteArray())
            }
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ── CSV builder ──────────────────────────────────────────────────────────

    private fun buildCsvContent(data: List<SensorResponse>): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()

        // Header
        sb.appendLine("No,Waktu,pH,TDS (ppm),Suhu (°C),Turbidity (NTU),Status,Latitude,Longitude")

        // Rows — terbaru di atas
        data.reversed().forEachIndexed { index, item ->
            val time   = item.timestamp?.let { sdf.format(Date(it * 1000)) } ?: "-"
            val ph     = item.ph?.let { "%.2f".format(it) } ?: "-"
            val tds    = item.tds?.let { "%.1f".format(it) } ?: "-"
            val temp   = item.temperature?.let { "%.1f".format(it) } ?: "-"
            val turb   = item.turbidity?.let { "%.2f".format(it) } ?: "-"
            val status = QualityWaterCheck.evaluate(item.ph, item.tds, item.temperature, item.turbidity).label
            val lat    = item.lat?.toString() ?: "-"
            val lon    = item.lon?.toString() ?: "-"

            sb.appendLine("${index + 1},$time,$ph,$tds,$temp,$turb,\"$status\",$lat,$lon")
        }

        return sb.toString()
    }

    // ── OutputStream factory: MediaStore (API 29+) vs legacy (API < 29) ──────

    private fun openOutputStream(context: Context, fileName: String): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ → MediaStore, tidak butuh WRITE permission
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return null

            val stream = resolver.openOutputStream(uri)

            // Tandai file selesai ditulis
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            stream
        } else {
            // Android 9 ke bawah → tulis langsung ke Downloads folder
            // WRITE_EXTERNAL_STORAGE sudah ada di AndroidManifest
            @Suppress("DEPRECATION")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            FileOutputStream(File(downloadsDir, fileName))
        }
    }
}