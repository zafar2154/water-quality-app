package com.example.waterquality.ui.screen.homepage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.waterquality.R
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.ui.component.NavBar
import com.example.waterquality.ui.component.ChartView
import com.example.waterquality.ui.component.CurrentData
import com.example.waterquality.ui.component.QualityCheck
import com.example.waterquality.ui.component.SensorType

@Composable
fun HomeScreen(
    viewModel: SensorViewModel = hiltViewModel(),
    onLogout: () -> Unit = {},
    username: String,
    email: String,
){
    val uiState by viewModel.sensorData.collectAsState()
    HomeScreenContent(
        uiState = uiState,
        onLogout = onLogout,
        username = username,
        email = email,
        onSaveIpAddress = {ip -> viewModel.saveIpAddress(ip)}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onLogout: () -> Unit = {},
    username: String,
    email: String,
    onSaveIpAddress: (String) -> Unit
) {
    val sensorData = uiState.currentData
    val scrollState = rememberScrollState()
    val phIcon = painterResource(R.drawable.logo_ph)
    val tdsIcon = painterResource(R.drawable.logo_tds)
    val tempIcon = painterResource(R.drawable.logo_temp)

    // CATATAN:
    // Kita MENGHAPUS 'LaunchedEffect(Unit) { viewModel.loadIpAndStart(context) }'
    // karena ViewModel sekarang sudah pintar dan otomatis berjalan sendiri.

    Scaffold(
        // Kita melempar viewModel ke NavBar agar NavBar bisa mengakses fungsi ganti IP
        topBar = { NavBar(onSaveIpAddress = onSaveIpAddress, onLogout = onLogout, username = username, email = email) },
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
                        values = uiState.phHistory,
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
                        values = uiState.tempHistory,
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
                values = uiState.tdsHistory,
                ymax = 1000f,
                granularityY = 100f,
                labelCount = 10,
                lineColor = android.graphics.Color.GREEN
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        uiState = HomeUiState(
            currentData = SensorResponse(ph = 7.2f, tds = 150f, temperature = 28.5f),
            phHistory = listOf(6.8f, 7.0f, 7.1f, 7.2f, 7.3f),
            tdsHistory = listOf(140f, 145f, 148f, 150f),
            tempHistory = listOf(28.0f, 28.2f, 28.4f, 28.5f)
        ),
        username = "Afit",
        email = "afit@example.com",
        onLogout = {},
        onSaveIpAddress = {}
    )
}