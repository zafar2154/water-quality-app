package com.example.waterquality

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.ui.theme.WaterQualityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaterQualityTheme {
                WaterQualityApp()
                }
            }
        }
    }


@Composable
fun WaterQualityApp(viewModel: SensorViewModel = viewModel()) {
    val sensorData by viewModel.sensorData.collectAsState()
    var phHistory by remember { mutableStateOf(listOf<Float>()) }

    LaunchedEffect(sensorData) {
        phHistory = phHistory.takeLast(20) + sensorData.ph
    }

    Column(modifier = Modifier.padding(20.dp)) {
        Text("pH: ${"%.2f".format(sensorData.ph)}")
        Text("TDS: ${sensorData.tds} ppm")
        Text("Temp: ${sensorData.temperature} °C")
        Spacer(Modifier.height(16.dp))

        Button(onClick = { viewModel.simulateData() }) {
            Text("Update Data")
        }

        Spacer(Modifier.height(24.dp))

        PHChartView(phValues = phHistory, modifier = Modifier.height(300.dp).fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    WaterQualityTheme {
        WaterQualityApp()
    }
}