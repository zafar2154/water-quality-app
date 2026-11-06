package com.example.waterquality.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterquality.ui.theme.WaterQualityTheme
import com.example.waterquality.utils.DeviceStatus
import com.example.waterquality.utils.TimestampFormat
import com.example.waterquality.utils.formatElapsed
import kotlinx.coroutines.delay

private val OnlineGreen = Color(0xFF4CAF50)
private val OfflineBg = Color(0xFFFFE0B2)
private val OfflineFg = Color(0xFF8A4B00)

/**
 * Menampilkan status perangkat & koneksi:
 * - HP tidak terhubung ke Firebase -> banner abu-abu (status perangkat tidak bisa diketahui)
 * - Perangkat offline              -> banner oranye + waktu data terakhir
 * - Perangkat online               -> indikator hijau kecil
 * - Menunggu data pertama          -> teks kecil
 */
@Composable
fun DeviceStatusBanner(
    deviceStatus: DeviceStatus,
    lastSeenMillis: Long?,
    isFirebaseConnected: Boolean,
    modifier: Modifier = Modifier
) {
    // Jam lokal yang hanya membuat composable ini recompose tiap detik,
    // bukan seluruh HomeScreen (chart tidak ikut di-reset).
    val now by produceState(System.currentTimeMillis()) {
        while (true) {
            delay(1_000)
            value = System.currentTimeMillis()
        }
    }
    val lastSeenText = lastSeenMillis?.let {
        "${TimestampFormat(it)} (${formatElapsed(now - it)})"
    }

    when {
        !isFirebaseConnected -> WarningBanner(
            icon = Icons.Default.CloudOff,
            title = "Tidak terhubung ke server",
            subtitle = "Periksa koneksi internet HP. " +
                (lastSeenText?.let { "Data terakhir: $it" } ?: "Belum ada data."),
            background = MaterialTheme.colorScheme.surfaceVariant,
            foreground = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )

        deviceStatus == DeviceStatus.OFFLINE -> WarningBanner(
            icon = Icons.Default.SensorsOff,
            title = "Perangkat offline",
            subtitle = "Data di bawah bukan data terkini. Terakhir: ${lastSeenText ?: "-"}",
            background = OfflineBg,
            foreground = OfflineFg,
            modifier = modifier
        )

        deviceStatus == DeviceStatus.ONLINE -> Row(
            modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(
                Modifier
                    .size(8.dp)
                    .background(OnlineGreen, CircleShape)
            )
            Text(
                text = "  Perangkat online",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        else -> Text(
            text = "Menunggu data dari perangkat…",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun WarningBanner(
    icon: ImageVector,
    title: String,
    subtitle: String,
    background: Color,
    foreground: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(background, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = foreground)
        Column(Modifier.padding(start = 12.dp)) {
            Text(title, color = foreground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = foreground, fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceStatusBannerPreview() {
    val now = System.currentTimeMillis()
    WaterQualityTheme {
        Column(Modifier.padding(12.dp)) {
            DeviceStatusBanner(DeviceStatus.ONLINE, now - 3_000, isFirebaseConnected = true)
            DeviceStatusBanner(DeviceStatus.OFFLINE, now - 5 * 60_000, isFirebaseConnected = true)
            DeviceStatusBanner(DeviceStatus.OFFLINE, now - 60_000, isFirebaseConnected = false)
            DeviceStatusBanner(DeviceStatus.WAITING, null, isFirebaseConnected = true)
        }
    }
}
