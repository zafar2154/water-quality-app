package com.example.waterquality.ui.screen.homepage

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.ui.screen.homepage.SensorViewModel
import com.example.waterquality.ui.theme.WaterQualityTheme
import com.example.waterquality.utils.ExportService
import com.example.waterquality.utils.QualityWaterCheck
import com.example.waterquality.utils.TimestampFormat
import com.example.waterquality.utils.WaterStatus
import kotlinx.coroutines.launch

// ── Stateful wrapper ────────────────────────────────────────────────────────
@Composable
fun HistoryScreen(
    viewModel: SensorViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.sensorData.collectAsState()
    HistoryScreenContent(
        historyList = uiState.historyList,
        onBack = onBack
    )
}

// ── Stateless UI ────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    historyList: List<SensorResponse>,
    onBack: () -> Unit
) {
    val context      = LocalContext.current
    val scope        = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }
    var isExporting  by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            doExport(context, historyList, scope, snackbarHost) { isExporting = it }
        } else {
            scope.launch {
                snackbarHost.showSnackbar("Izin penyimpanan ditolak. Ekspor dibatalkan.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Data Sensor", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Tombol ekspor di AppBar — aktif hanya jika ada data
                    IconButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                permissionLauncher.launch(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            } else {
                                doExport(
                                    context, historyList, scope, snackbarHost
                                ) { isExporting = it }
                            }
                        },
                        enabled = historyList.isNotEmpty() && !isExporting
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Ekspor CSV",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── Jumlah record + info ekspor ───────────────────────────────
            if (historyList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${historyList.size} data tersimpan (maks 50)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isExporting) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(6.dp))
                            Text("Mengekspor…", fontSize = 12.sp)
                        }
                    } else {
                        Text(
                            text = "↑ Tekan ikon unduh untuk ekspor CSV",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (historyList.isEmpty()) {
                // ── Empty state ───────────────────────────────────────────
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada data tersimpan.\nData akan muncul setiap ~50 detik.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                // ── Header tabel ──────────────────────────────────────────
                TableHeader()

                // ── Baris data ────────────────────────────────────────────
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(historyList.reversed()) { index, item ->
                        HistoryRow(item = item, isEven = index % 2 == 0)
                    }
                }
            }
        }
    }
}

// ── Helper: jalankan ekspor di coroutine + tampilkan hasil ──────────────────
private fun doExport(
    context: android.content.Context,
    historyList: List<SensorResponse>,
    scope: kotlinx.coroutines.CoroutineScope,
    snackbarHost: SnackbarHostState,
    setExporting: (Boolean) -> Unit
) {
    scope.launch {
        setExporting(true)
        val fileName =  ExportService.exportToCsv(context, historyList)
        setExporting(false)
        if (fileName != null) {
            snackbarHost.showSnackbar(
                message = "✓ Tersimpan di Downloads/$fileName",
                duration = SnackbarDuration.Long
            )
        } else {
            snackbarHost.showSnackbar("Ekspor gagal. Coba lagi.")
        }
    }
}

// ── Table header row ────────────────────────────────────────────────────────
@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("Waktu",     weight = 1.6f)
        HeaderCell("pH",        weight = 0.8f)
        HeaderCell("TDS\n(ppm)",weight = 0.9f)
        HeaderCell("Suhu\n(°C)",weight = 0.9f)
        HeaderCell("NTU",       weight = 0.8f)
        HeaderCell("Status",    weight = 1.2f)
    }
}

@Composable
fun RowScope.HeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        lineHeight = 14.sp
    )
}

// ── Single data row ─────────────────────────────────────────────────────────
@Composable
fun HistoryRow(item: SensorResponse, isEven: Boolean) {
    val status = QualityWaterCheck.evaluate(item.ph, item.tds, item.temperature, item.turbidity)
    val rowBg  = if (isEven) Color.Transparent
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell(TimestampFormat(item.timestamp), weight = 1.6f)
        DataCell(item.ph?.let { "%.2f".format(it) } ?: "-",          weight = 0.8f)
        DataCell(item.tds?.let { "%.1f".format(it) } ?: "-",         weight = 0.9f)
        DataCell(item.temperature?.let { "%.1f".format(it) } ?: "-", weight = 0.9f)
        DataCell(item.turbidity?.let { "%.2f".format(it) } ?: "-",   weight = 0.8f)

        // Status badge
        Box(
            modifier = Modifier
                .weight(1.2f)
                .padding(horizontal = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            StatusBadge(status)
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}

@Composable
fun RowScope.DataCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontSize = 11.sp,
        textAlign = TextAlign.Center
    )
}

// ── Colored status badge ────────────────────────────────────────────────────
@Composable
fun StatusBadge(status: WaterStatus) {
    val label = when (status) {
        WaterStatus.SAFE    -> "Aman"
        WaterStatus.WARNING -> "Waspada"
        WaterStatus.DANGER  -> "Bahaya"
        WaterStatus.UNKNOWN -> "..."
    }
    Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = status.composeColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(6.dp)
            )
            .border(1.dp, status.composeColor, RoundedCornerShape(6.dp))
            .padding(vertical = 3.dp, horizontal = 4.dp)
    )
}

// ── Preview ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    WaterQualityTheme {
        HistoryScreenContent(
            historyList = listOf(
                SensorResponse(timestamp = 1746600000L, ph = 7.1f, tds = 210f,  temperature = 27.5f, turbidity = 1.2f),
                SensorResponse(timestamp = 1746600050L, ph = 6.3f, tds = 520f,  temperature = 29.0f, turbidity = 3.8f),
                SensorResponse(timestamp = 1746600100L, ph = 8.9f, tds = 1100f, temperature = 35.0f, turbidity = 5.1f),
                SensorResponse(timestamp = 1746600150L, ph = 7.4f, tds = 180f,  temperature = 28.0f, turbidity = 0.8f),
            ),
            onBack = {}
        )
    }
}