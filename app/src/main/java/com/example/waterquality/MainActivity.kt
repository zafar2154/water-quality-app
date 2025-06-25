package com.example.waterquality

import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.ui.theme.WaterQualityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {
            WaterQualityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WaterQualityApp()
                }
                }
            }
        }
    }




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterQualityApp(viewModel: SensorViewModel = viewModel()) {
    val sensorData by viewModel.sensorData.collectAsState()
    val scrollState = rememberScrollState()
    var phHistory by remember { mutableStateOf(listOf<Float>()) }
    var tdsHistory by remember { mutableStateOf(listOf<Float>()) }
    var tempHistory by remember { mutableStateOf(listOf<Float>()) }
    val phIcon = painterResource(R.drawable.screenshot_2025_06_12_232854_removebg_preview)
    val tdsIcon = painterResource(R.drawable.screenshot_2025_06_12_233036_removebg_preview)
    val tempIcon = painterResource(R.drawable.pngtreevector_temperature_icon_4159827)
//    val items = listOf(
//        Triple(phIcon, "${sensorData.ph}", "PH"),
//        Triple(tdsIcon, "${sensorData.tds} PPM", "TDS"),
//        Triple(tempIcon, "${sensorData.temperature}", "Temperature")
//    )
    LaunchedEffect(sensorData) {
        phHistory = phHistory + sensorData.ph
        tdsHistory = tdsHistory + sensorData.tds
        tempHistory = tempHistory + sensorData.temperature
    }
    LaunchedEffect(Unit) {
        viewModel.fetchSensor()
    }

    Scaffold (
        topBar = {
        TopAppBar(
            title = { Text("WaterQuality", color = Color.White)},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .border(1.dp, Color.DarkGray)
        )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) {
innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(12.dp)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            Row (modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            ){
                    CurrentData(phIcon, "${sensorData.ph}", "PH")
                    CurrentData(tdsIcon, "${sensorData.tds} PPM", "TDS")
                    CurrentData(tempIcon, "${sensorData.temperature}", "Temperature")
            }
            Spacer(Modifier.height(12.dp))
//        Button(onClick = { viewModel.fetchSensor() }) {
//            Text("Update Data")
//        }
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp, color =Color.Black)
            Spacer(Modifier.height(24.dp))

            Row (modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .height(200.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column (modifier = Modifier
                    .width(180.dp)
                    .height(200.dp)
                    .weight(1f)
                ){
                    Text("PH Graph", modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .align(Alignment.CenterHorizontally)
                        .wrapContentSize()
                    )
                    ChartView(
                        title = "PH Level",
                        values = phHistory,
                        ymax = 14f,
                        granularityY = 1f,
                        labelCount = 7,
                        lineColor = android.graphics.Color.BLUE,
                        )
                }
                Column (modifier = Modifier
                    .height(200.dp)
                    .weight(1f)
                ) {
                    Text("Temp Chart", modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                    )
                    ChartView(
                        title = "Temperature Level (C)",
                        values = tempHistory,
                        ymax = 100f,
                        granularityY = 10f,
                        labelCount = 50,
                        lineColor = android.graphics.Color.YELLOW,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
              }

            Spacer(Modifier.height(24.dp))
            Text("TDS Graph", modifier = Modifier
                .width(IntrinsicSize.Max)
                .align(Alignment.CenterHorizontally)
            )
            ChartView(
                title = "TDS Level (ppm)",
                values = tdsHistory,
                ymax = 1000f,
                granularityY = 100f,
                labelCount = 10,
                lineColor = android.graphics.Color.GREEN,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Preview(
    showBackground = true,

)
@Composable
fun PreviewApp() {
    WaterQualityTheme {
        WaterQualityApp()
    }
}