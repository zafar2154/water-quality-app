package com.example.waterquality.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.waterquality.data.model.SensorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportService {

    private const val TAG = "ExportService"

    /**
     * Locale tetap untuk angka di CSV. Locale perangkat (misal id_ID) memakai koma
     * sebagai pemisah desimal ("7,10") yang bentrok dengan pemisah kolom CSV.
     */
    private val NUMBER_LOCALE = Locale.US

    /** BOM agar Excel membaca file sebagai UTF-8 (karakter "°" tampil benar). */
    private val UTF8_BOM = Char(0xFEFF).toString()

    /**
     * Ekspor list SensorResponse ke file CSV di folder Downloads.
     * Dijalankan di Dispatchers.IO, aman dipanggil dari Main thread.
     * Return: nama file jika berhasil, null jika gagal.
     */
    suspend fun exportToCsv(context: Context, data: List<SensorResponse>): String? =
        withContext(Dispatchers.IO) {
            if (data.isEmpty()) return@withContext null

            val fileName = "water_quality_${
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            }.csv"
            val bytes = (UTF8_BOM + buildCsvContent(data)).toByteArray(Charsets.UTF_8)

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    writeViaMediaStore(context, fileName, bytes)
                } else {
                    writeLegacy(fileName, bytes)
                }
                fileName
            } catch (e: Exception) {
                Log.e(TAG, "Ekspor CSV gagal", e)
                null
            }
        }

    // ── CSV builder ──────────────────────────────────────────────────────────

    private fun buildCsvContent(data: List<SensorResponse>): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()

        // Header
        sb.appendLine("No,Waktu,pH,TDS (ppm),Suhu (°C),Turbidity (NTU),WQI,Kategori,Latitude,Longitude")

        // Rows — terbaru di atas
        data.reversed().forEachIndexed { index, item ->
            val time   = item.timestamp?.let { sdf.format(Date(timestampToMillis(it))) } ?: "-"
            val ph     = item.ph.fmt(2)
            val tds    = item.tds.fmt(1)
            val temp   = item.temperature.fmt(1)
            val turb   = item.turbidity.fmt(2)
            val wqi    = item.wqi.fmt(2)
            val status = csvQuote(item.kategori ?: "-")
            val lat    = item.lat?.toString() ?: "-"
            val lon    = item.lon?.toString() ?: "-"

            sb.appendLine("${index + 1},$time,$ph,$tds,$temp,$turb,$wqi,$status,$lat,$lon")
        }

        return sb.toString()
    }

    /** Format angka dengan titik desimal; null / NaN -> "-". */
    private fun Float?.fmt(decimals: Int): String =
        if (this == null || isNaN()) "-" else "%.${decimals}f".format(NUMBER_LOCALE, this)

    /** Bungkus teks dengan tanda kutip dan escape kutip di dalamnya (RFC 4180). */
    private fun csvQuote(value: String): String = "\"${value.replace("\"", "\"\"")}\""

    // ── Writer: MediaStore (API 29+) vs legacy (API < 29) ────────────────────

    /**
     * Android 10+: file dibuat dengan IS_PENDING=1, diisi, BARU kemudian
     * IS_PENDING=0. Jika penulisan gagal, entri dihapus agar tidak ada
     * file kosong/rusak tertinggal di Downloads.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun writeViaMediaStore(context: Context, fileName: String, bytes: ByteArray) {
        val resolver = context.contentResolver
        val pendingValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri: Uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, pendingValues)
            ?: error("Gagal membuat entri MediaStore")

        try {
            val stream = resolver.openOutputStream(uri) ?: error("Gagal membuka output stream")
            stream.use { it.write(bytes) }

            val doneValues = ContentValues().apply {
                put(MediaStore.Downloads.IS_PENDING, 0)
            }
            resolver.update(uri, doneValues, null, null)
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
    }

    /** Android 9 ke bawah: tulis langsung ke folder Downloads (butuh WRITE_EXTERNAL_STORAGE). */
    private fun writeLegacy(fileName: String, bytes: ByteArray) {
        @Suppress("DEPRECATION")
        val downloadsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
        if (!downloadsDir.exists()) downloadsDir.mkdirs()
        val file = File(downloadsDir, fileName)
        try {
            FileOutputStream(file).use { it.write(bytes) }
        } catch (e: Exception) {
            file.delete()
            throw e
        }
    }
}
