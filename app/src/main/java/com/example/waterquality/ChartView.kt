package com.example.waterquality

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.ui.theme.WaterQualityTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun ChartView(
    title: String,
    lineColor: Int,
    ymax: Float,
    granularityY: Float,
    labelCount: Int,
    values: List<Float>,
    modifier: Modifier = Modifier
) {
    val windowSize = 10
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)

                axisLeft.apply {
                    axisMinimum = 0f
                    axisMaximum = ymax
                    granularity = granularityY
                    isGranularityEnabled = true
                    setLabelCount(labelCount, false)
                }

                axisRight.isEnabled = false

                xAxis.apply {
                    granularity = 1f
                    setDrawGridLines(true)
                    setDrawAxisLine(true)
                    setDrawLabels(true)
                    setLabelCount(10, false)
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }

                }
            }
        },
        update = { chart ->
            val entries = values.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }
            val dataSet = LineDataSet(entries, title).apply {
                color = lineColor
                setCircleColor(lineColor)
                lineWidth = 2f
                circleRadius = 3f
                setDrawValues(false)
            }
            chart.data = LineData(dataSet)

            val lastX = entries.lastOrNull()?.x ?: 0f
            val minX = (lastX - windowSize).coerceAtLeast(0f)
            val maxX = minX + windowSize

            chart.xAxis.axisMinimum = minX
            chart.xAxis.axisMaximum = maxX
            chart.moveViewToX(minX)
            chart.invalidate()
        },
        modifier = modifier
            .height(200.dp)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewChart(viewModel: SensorViewModel = viewModel()) {
    WaterQualityTheme {
        val sensorData by viewModel.sensorData.collectAsState()
        var phHistory by remember { mutableStateOf(listOf<Float>()) }
        var tdsHistory by remember { mutableStateOf(listOf<Float>()) }
        var tempHistory by remember { mutableStateOf(listOf<Float>()) }
        LaunchedEffect(sensorData) {
            phHistory = phHistory.takeLast(10) + sensorData.ph
            tdsHistory = tdsHistory.takeLast(10) + sensorData.tds
            tempHistory = tempHistory.takeLast(10) + sensorData.temperature
        }
        Column {
            ChartView(
                title = "TDS Level (ppm)",
                values = tdsHistory,
                ymax = 1000f,
                granularityY = 100f,
                labelCount = 100,
                lineColor = Color.GREEN
            )
        }
    }
}