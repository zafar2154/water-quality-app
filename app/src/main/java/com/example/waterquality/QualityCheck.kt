package com.example.waterquality

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterquality.ui.theme.BgGreen
import com.example.waterquality.ui.theme.WaterQualityTheme

@Composable
fun QualityCheck(tds: Float, ph: Float, temp: Float) {
    val statusText = when {
        ph in 6.5..9.0 && tds < 1000 && temp in 21.0 .. 40.0-> "Aman"
        else -> "Tidak Aman"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF00ACC1), RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF00796B)
        )
    }

}

@Preview
@Composable
fun PreviewQualityCheck() {
    WaterQualityTheme {
        QualityCheck(ph = 7.0f, tds = 400f, temp = 100f)
    }
}