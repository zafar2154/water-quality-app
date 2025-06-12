package com.example.waterquality

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun CurrentData(viewModel: SensorViewModel = viewModel()) {
    val phIcon = painterResource(R.drawable.screenshot_2025_06_12_232854_removebg_preview)
    val tdsIcon = painterResource(R.drawable.screenshot_2025_06_12_233036_removebg_preview)
    val tempIcon = painterResource(R.drawable.pngtreevector_temperature_icon_4159827)
    val sensorData by viewModel.sensorData.collectAsState()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.height(80.dp)
        ) {
            Image(
                painter = phIcon,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(40.dp)
                    .aspectRatio(1f)
                    .padding(0.dp)
            )
            Text(
                text = "${sensorData.ph}",
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .weight(2f)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Spacer(Modifier.width(20.dp))
        Column(modifier = Modifier.height(80.dp)
        ) {
            Image(
                painter = tdsIcon,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(40.dp)
                    .aspectRatio(1f)
                    .padding(0.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Text(
                text = "${sensorData.tds} ppm",
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .weight(2f)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Spacer(Modifier.width(20.dp))
        Column (modifier = Modifier.height(80.dp)){
            Image(
                painter = tempIcon,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(40.dp)
                    .aspectRatio(1f)
                    .padding(0.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Text(
                text = "${sensorData.temperature} °C",
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .weight(2f)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
@Composable
fun WaterQualityApp(viewModel: SensorViewModel = viewModel()) {
    val sensorData by viewModel.sensorData.collectAsState()
    val scrollState = rememberScrollState()
    var phHistory by remember { mutableStateOf(listOf<Float>()) }
    var tdsHistory by remember { mutableStateOf(listOf<Float>()) }
    var tempHistory by remember { mutableStateOf(listOf<Float>()) }

    LaunchedEffect(sensorData) {
        phHistory = phHistory.takeLast(10) + sensorData.ph
        tdsHistory = tdsHistory.takeLast(10) + sensorData.tds
        tempHistory = tempHistory.takeLast(10) + sensorData.temperature
    }

    Column(modifier = Modifier
        .padding(20.dp, top = 30.dp)
        .verticalScroll(scrollState)) {
        CurrentData()
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.simulateData() }) {
            Text("Update Data")
        }

        Spacer(Modifier.height(24.dp))

        PHChartView(phValues = phHistory, modifier = Modifier
            .height(300.dp)
            .fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        TDSChartView(tdsValues = tdsHistory, modifier = Modifier
            .height(300.dp)
            .fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        TempChartView(tempValues =  tempHistory, modifier = Modifier
            .height(300.dp)
            .fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    WaterQualityTheme {
        WaterQualityApp()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrentData() {
    WaterQualityTheme {
        CurrentData()
    }
}