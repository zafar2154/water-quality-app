package com.example.waterquality.ui.screen.homepage

import com.example.waterquality.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.ui.component.NavBar
import com.example.waterquality.ui.component.ChartView
import com.example.waterquality.ui.component.CurrentData
import com.example.waterquality.ui.component.QualityCheck
import com.example.waterquality.ui.component.SensorType
import com.example.waterquality.ui.screen.auth.AuthViewModel
import com.example.waterquality.ui.theme.WaterQualityTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SensorViewModel = viewModel(),
    onLogout: () -> Unit = {},
    username: String,
    email: String
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val scrollState = rememberScrollState()
    var phHistory by remember { mutableStateOf(listOf<Float>()) }
    var tdsHistory by remember { mutableStateOf(listOf<Float>()) }
    var tempHistory by remember { mutableStateOf(listOf<Float>()) }
    val phIcon = painterResource(R.drawable.logo_ph)
    val tdsIcon = painterResource(R.drawable.logo_tds)
    val tempIcon = painterResource(R.drawable.logo_temp)

    val context = LocalContext.current

    LaunchedEffect(sensorData) {
        sensorData.ph?.takeIf { !it.isNaN() }?.let { phHistory = phHistory + it }
        sensorData.tds?.takeIf { !it.isNaN() }?.let { tdsHistory = tdsHistory + it }
        sensorData.temperature?.takeIf { !it.isNaN() }?.let { tempHistory = tempHistory + it }
    }

    LaunchedEffect(Unit) {
        viewModel.loadIpAndStart(context)
    }

    Scaffold(
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            ) {
                CurrentData(phIcon, sensorData.ph, SensorType.pH)
                CurrentData(tdsIcon, sensorData.tds, SensorType.TDS)
                CurrentData(tempIcon, sensorData.temperature, SensorType.Temperature)
            }

            QualityCheck(sensorData.ph, sensorData.tds, sensorData.temperature)

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.Black)
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                        labelCount = 50,
                        lineColor = android.graphics.Color.YELLOW,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

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
