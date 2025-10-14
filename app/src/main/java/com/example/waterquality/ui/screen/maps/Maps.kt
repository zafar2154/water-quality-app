package com.example.waterquality.ui.screen.maps

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    points: List<Pair<Double, Double>> = emptyList()
) {
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

                // Tambahkan marker dari parameter points
                points.forEach { (lat, lon) ->
                    val marker = Marker(this)
                    marker.position = GeoPoint(lat, lon)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = "Sensor @ ($lat, $lon)"
                    overlays.add(marker)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewOsmMapView() {
    // contoh titik koordinat sensor (Jakarta & Bandung)
    val dummyPoints = listOf(
        -6.2088 to 106.8456, // Jakarta
        -6.9175 to 107.6191  // Bandung
    )

    OsmMapView(points = dummyPoints)
}
