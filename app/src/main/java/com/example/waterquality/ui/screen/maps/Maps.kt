// In zafar2154/.../ui/screen/maps/Maps.kt

package com.example.waterquality.ui.screen.maps

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.waterquality.R
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.data.model.SensorType
import com.example.waterquality.ui.component.CurrentData
import com.example.waterquality.ui.component.QualityCheck
import com.example.waterquality.utils.WaterStatus
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    points: List<SensorResponse> = emptyList()
) {
    val isPreview = LocalInspectionMode.current
    var selectedSensor by remember { mutableStateOf<SensorResponse?>(null) }
    Box(modifier = modifier.fillMaxSize()) {
        if (isPreview) {
            // Render a placeholder in Android Studio Preview to avoid osmdroid initialization crashes
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Map View Placeholder")
            }
        } else {
            AndroidView(
                factory = { context ->
                    // load konfigurasi osmdroid
                    Configuration.getInstance().load(
                        context,
                        context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                    )

                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        // set posisi awal (Jakarta)
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(-6.2088, 106.8456))
                    }
                },
                update = { mapView ->
                    // Bersihkan marker lama agar tidak menumpuk saat ada pembaruan data
                    mapView.overlays.clear()

                    points.forEach { data ->
                        val lat = data.lat
                        val lon = data.lon

                        if (lat != null && lon != null) {
                            val marker = Marker(mapView)
                            marker.position = GeoPoint(lat, lon)
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            marker.title = "Sensor at ($lat, $lon)"

                            val status = WaterStatus.fromKategori(data.kategori)

                            val icon = mapView.repository.defaultMarkerIcon.constantState?.newDrawable()?.mutate()

                            icon?.let {
                                it.colorFilter = PorterDuffColorFilter(
                                    status.androidColor, PorterDuff.Mode.SRC_IN)
                                marker.icon = it
                            }

                            // 3. Tambahkan event klik pada marker
                            marker.setOnMarkerClickListener { _, _ ->
                                selectedSensor = data // Simpan data ke state agar Dialog muncul
                                true // Return true menandakan event klik sudah ditangani
                            }
                            mapView.overlays.add(marker)
                        }
                    }
                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        selectedSensor?.let { sensorData ->
            SensorInfoDialog(
                data = sensorData,
                onDismiss = { selectedSensor = null } // Tutup dialog jika menekan di luar / tombol tutup
            )
        }
    }
}

@Composable
fun SensorInfoDialog(data: SensorResponse, onDismiss: () -> Unit) {
    val phIcon = painterResource(R.drawable.logo_ph)
    val tdsIcon = painterResource(R.drawable.logo_tds)
    val tempIcon = painterResource(R.drawable.logo_temp)
    val turbidityIcon = painterResource(R.drawable.logo_turbidity)

    // Memanggil fungsi evaluasi kualitas air dari utils Anda
    val status = WaterStatus.fromKategori(data.kategori)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Detail Data Kualitas Air") },
        text = {
            Column {
                Text("Latitude: ${data.lat ?: "-"}")
                Text("Longitude: ${data.lon ?: "-"}")
                Row (horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()){
                    CurrentData(phIcon, data.ph, SensorType.PH, modifier = Modifier.height(12.dp).width(10.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    CurrentData(tdsIcon, data.tds, SensorType.TDS, modifier = Modifier.height(12.dp).width(10.dp))
                }
                Row (horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()){
                    CurrentData(turbidityIcon, data.turbidity, SensorType.Turbidity, modifier = Modifier.height(12.dp).width(10.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    CurrentData(tempIcon, data.temperature, SensorType.Temperature, modifier = Modifier.height(12.dp).width(10.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Menampilkan label klasifikasi dengan warna yang sesuai (misal: Hijau/Merah)
                QualityCheck(kategori = data.kategori)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewOsmMapView() {
    // Contoh data dummy (Jakarta & Bandung) beserta nilai sensor
    val dummyPoints = listOf(
        SensorResponse(
            ph = 7.0f, tds = 250f, temperature = 25f, turbidity = 0.5f,
            lat = -6.2088, lon = 106.8456
        ),
        SensorResponse(
            ph = 11.0f, tds = 1200f, temperature = 45f, turbidity = 6.0f,
            lat = -6.9175, lon = 107.6191
        )
    )

    OsmMapView(points = dummyPoints)
}