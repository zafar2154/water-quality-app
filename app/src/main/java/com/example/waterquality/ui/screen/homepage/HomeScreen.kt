package com.example.waterquality.ui.screen.homepage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.R
import com.example.waterquality.ui.component.NavBar
import com.example.waterquality.ui.component.ChartView
import com.example.waterquality.ui.component.CurrentData
import com.example.waterquality.ui.component.QualityCheck
import com.example.waterquality.ui.component.SensorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // HILT MAGIC:
    // Fungsi viewModel() ini akan otomatis meminta Hilt untuk membuatkan
    // SensorViewModel beserta semua dependency-nya (Repository, ApiService, dll).
    viewModel: SensorViewModel = viewModel(),
    onLogout: () -> Unit = {},
    username: String,
    email: String
) {
    // Mengambil data dari StateFlow di ViewModel
    // gunakan lifecycle-aware collection jika memungkinkan, tapi collectAsState() cukup untuk dasar
    val sensorData by viewModel.sensorData.collectAsState()

    val scrollState = rememberScrollState()

    // State untuk grafik (History data sementara)
    var phHistory by remember { mutableStateOf(listOf<Float>()) }
    var tdsHistory by remember { mutableStateOf(listOf<Float>()) }
    var tempHistory by remember { mutableStateOf(listOf<Float>()) }

    val phIcon = painterResource(R.drawable.logo_ph)
    val tdsIcon = painterResource(R.drawable.logo_tds)
    val tempIcon = painterResource(R.drawable.logo_temp)

    // LOGIC GRAFIK: Menambahkan data ke list setiap kali ada data sensor baru
    LaunchedEffect(sensorData) {
        sensorData.ph?.takeIf { !it.isNaN() }?.let { phHistory = phHistory + it }
        sensorData.tds?.takeIf { !it.isNaN() }?.let { tdsHistory = tdsHistory + it }
        sensorData.temperature?.takeIf { !it.isNaN() }?.let { tempHistory = tempHistory + it }
    }

    // CATATAN:
    // Kita MENGHAPUS 'LaunchedEffect(Unit) { viewModel.loadIpAndStart(context) }'
    // karena ViewModel sekarang sudah pintar dan otomatis berjalan sendiri.

    Scaffold(
        // Kita melempar viewModel ke NavBar agar NavBar bisa mengakses fungsi ganti IP
        topBar = { NavBar(viewModel, onLogout = onLogout, username = username, email = email) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(12.dp)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            // Bagian Atas: Kartu Data Saat Ini
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            ) {
                CurrentData(phIcon, sensorData.ph, SensorType.pH)
                CurrentData(tdsIcon, sensorData.tds, SensorType.TDS)
                CurrentData(tempIcon, sensorData.temperature, SensorType.Temperature)
            }

            // Indikator Kualitas Air
            QualityCheck(sensorData.ph, sensorData.tds, sensorData.temperature)

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.Black)
            Spacer(Modifier.height(24.dp))

            // Bagian Grafik
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Grafik pH
                Column(
                    modifier = Modifier
                        .width(180.dp)
                        .height(200.dp)
                        .weight(1f)
                ) {
                    Text("PH Graph", modifier = Modifier.align(Alignment.CenterHorizontally))
                    ChartView(
                        title = "PH Level",
                        values = phHistory,
                        ymax = 14f,
                        granularityY = 1f,
                        labelCount = 7,
                        lineColor = android.graphics.Color.BLUE,
                    )
                }

                // Grafik Suhu
                Column(
                    modifier = Modifier
                        .height(200.dp)
                        .weight(1f)
                ) {
                    Text("Temp Chart", modifier = Modifier.align(Alignment.CenterHorizontally))
                    ChartView(
                        title = "Temperature Level (C)",
                        values = tempHistory,
                        ymax = 100f,
                        granularityY = 10f,
                        labelCount = 10, // Disesuaikan agar tidak terlalu padat
                        lineColor = android.graphics.Color.YELLOW,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Grafik TDS
            Text("TDS Graph", modifier = Modifier.align(Alignment.CenterHorizontally))
            ChartView(
                title = "TDS Level (ppm)",
                values = tdsHistory,
                ymax = 1000f,
                granularityY = 100f,
                labelCount = 10,
                lineColor = android.graphics.Color.GREEN
            )
        }
    }
}