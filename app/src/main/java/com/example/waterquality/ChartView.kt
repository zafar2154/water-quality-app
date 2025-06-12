package com.example.waterquality

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun PHChartView(phValues: List<Float>, modifier: Modifier = Modifier) {
    val entries = phValues.mapIndexed { index, ph -> Entry(index.toFloat(), ph) }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)

                axisLeft.apply {
                    axisMinimum = 0f
                    axisMaximum = 14f
                    granularity = 1f
                    isGranularityEnabled = true
                    setLabelCount(15, false)

                }
                axisRight.isEnabled = false
                xAxis.apply {
                    axisMinimum = 0f         // Mulai dari 0 di kiri
                    granularity = 1f         // Jarak antar nilai (biar tidak acak)
                    setDrawGridLines(true)
                    setDrawAxisLine(true)
                    setDrawLabels(true)
                }

                val dataSet = LineDataSet(entries, "pH Level").apply {
                    color = Color.BLUE
                    setCircleColor(Color.BLUE)
                    lineWidth = 2f
                    circleRadius = 3f
                    setDrawValues(false)
                }

                data = LineData(dataSet)
                moveViewToX(0f)
                invalidate()
            }
        },

        update = { chart ->
            val dataSet = LineDataSet(entries, "pH Level").apply {
                color = Color.BLUE
                setCircleColor(Color.BLUE)
                lineWidth = 2f
                circleRadius = 3f
                setDrawValues(false)
            }
            chart.data = LineData(dataSet)
            chart.xAxis.axisMinimum = 0f
            chart.moveViewToX(0f)
            chart.invalidate()
        },
        modifier = modifier
    )
}


