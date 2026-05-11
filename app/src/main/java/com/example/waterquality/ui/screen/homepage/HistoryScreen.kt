package com.example.waterquality.ui.screen.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.ui.screen.homepage.SensorViewModel
import com.example.waterquality.ui.theme.WaterQualityTheme
import com.example.waterquality.utils.QualityWaterCheck
import com.example.waterquality.utils.TimestampFormat
import com.example.waterquality.utils.WaterStatus

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
    Scaffold(
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->

        if (historyList.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada data tersimpan.\nData akan muncul setiap ~50 detik.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            // Header + list
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ── Table header ──────────────────────────────────────────
                TableHeader()

                // ── Data rows ─────────────────────────────────────────────
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Tampilkan data terbaru di atas
                    itemsIndexed(historyList.reversed()) { index, item ->
                        HistoryRow(
                            item = item,
                            isEven = index % 2 == 0
                        )
                    }
                }
            }
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