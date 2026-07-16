// app/src/main/java/com/example/waterquality/ui/component/StatsCard.kt

package com.example.waterquality.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.ui.theme.WaterQualityTheme

// ── Data class untuk satu baris statistik ───────────────────────────────────
data class SensorStats(
    val label: String,
    val unit: String,
    val min: Float?,
    val max: Float?,
    val avg: Float?,
    val current: Float?
)

// ── Calculator ───────────────────────────────────────────────────────────────
fun computeStats(historyList: List<SensorResponse>): List<SensorStats> {
    fun stats(label: String, unit: String, values: List<Float?>, current: Float?): SensorStats {
        val clean = values.filterNotNull().filter { !it.isNaN() }
        return SensorStats(
            label   = label,
            unit    = unit,
            min     = clean.minOrNull(),
            max     = clean.maxOrNull(),
            avg     = if (clean.isEmpty()) null else clean.average().toFloat(),
            current = current
        )
    }

    val last = historyList.lastOrNull()
    return listOf(
        stats("pH",         "",    historyList.map { it.ph },          last?.ph),
        stats("TDS",        "ppm", historyList.map { it.tds },         last?.tds),
        stats("Suhu",       "°C",  historyList.map { it.temperature }, last?.temperature),
        stats("Turbidity",  "NTU", historyList.map { it.turbidity },   last?.turbidity)
    )
}

// ── Composable utama ─────────────────────────────────────────────────────────
@Composable
fun StatsCard(historyList: List<SensorResponse>) {
    val stats = computeStats(historyList)
    val recordCount = historyList.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        // ── Judul ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ringkasan Statistik",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            AnimatedContent(
                targetState = recordCount,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "record_count"
            ) { count ->
                Text(
                    text = "$count data",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        if (historyList.isEmpty()) {
            // ── Empty state ───────────────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Statistik tersedia setelah data pertama tersimpan (~50 detik)",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        } else {
            // ── Header kolom ──────────────────────────────────────────────
            StatsHeaderRow()
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )

            // ── Satu baris per parameter ──────────────────────────────────
            stats.forEachIndexed { i, stat ->
                StatsRow(stat = stat)
                if (i < stats.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

// ── Header kolom tabel ───────────────────────────────────────────────────────
@Composable
private fun StatsHeaderRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("Parameter", Modifier.weight(1.4f), fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold, color = Color.Gray)
        StatsHeaderCell("Saat ini", 1.2f)
        StatsHeaderCell("Min",      1f)
        StatsHeaderCell("Maks",     1f)
        StatsHeaderCell("Rata-rata",1.2f)
    }
}

@Composable
private fun RowScope.StatsHeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

// ── Satu baris statistik ─────────────────────────────────────────────────────
@Composable
private fun StatsRow(stat: SensorStats) {
    val fmt: (Float?) -> String = { v ->
        when {
            v == null  -> "-"
            stat.unit == "" -> "%.2f".format(v)         // pH: 2 desimal, tanpa unit
            else        -> "%.1f".format(v)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label parameter
        Text(
            text = if (stat.unit.isEmpty()) stat.label else "${stat.label} (${stat.unit})",
            modifier = Modifier.weight(1.4f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        // Nilai saat ini (live, dari historyList.last)
        AnimatedCell(value = fmt(stat.current), weight = 1.2f, highlight = true)
        // Min
        AnimatedCell(value = fmt(stat.min),     weight = 1f,   highlight = false)
        // Max
        AnimatedCell(value = fmt(stat.max),     weight = 1f,   highlight = false)
        // Avg
        AnimatedCell(value = fmt(stat.avg),     weight = 1.2f, highlight = false)
    }
}

// ── Cell dengan AnimatedContent supaya angka berganti dengan fade ─────────────
@Composable
private fun RowScope.AnimatedCell(value: String, weight: Float, highlight: Boolean) {
    AnimatedContent(
        targetState = value,
        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
        modifier = Modifier.weight(weight),
        label = "stat_cell_$value"
    ) { v ->
        Text(
            text = v,
            fontSize = 11.sp,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
            color = if (highlight) MaterialTheme.colorScheme.primary else Color.Unspecified,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun StatsCardPreview() {
    WaterQualityTheme {
        Column(Modifier.padding(12.dp)) {
            StatsCard(
                historyList = listOf(
                    SensorResponse(ph = 7.1f, tds = 210f, temperature = 27.5f, turbidity = 1.2f),
                    SensorResponse(ph = 6.8f, tds = 185f, temperature = 28.0f, turbidity = 0.9f),
                    SensorResponse(ph = 7.4f, tds = 240f, temperature = 26.8f, turbidity = 1.8f),
                    SensorResponse(ph = 6.3f, tds = 520f, temperature = 29.0f, turbidity = 3.8f),
                    SensorResponse(ph = 7.0f, tds = 200f, temperature = 28.5f, turbidity = 1.5f),
                )
            )

            Spacer(Modifier.height(16.dp))

            // Preview empty state
            StatsCard(historyList = emptyList())
        }
    }
}