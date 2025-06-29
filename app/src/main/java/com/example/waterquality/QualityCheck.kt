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
import com.example.waterquality.ui.theme.BgRed
import com.example.waterquality.ui.theme.WaterQualityTheme

fun isOverallSafe(ph: Float, tds: Float, temp: Float): Boolean =
    ph in 6.5f..9.0f && tds < 1000f && temp in 21f..40f


@Composable
fun QualityCheck(ph: Float, tds: Float, temp: Float) {
    val safe = isOverallSafe(ph, tds, temp)
    val statusText = if(safe) "Aman" else "tidak aman"
    val bgColor = if(safe) BgGreen else BgRed
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(bgColor.copy(0.4f), shape = RoundedCornerShape(12.dp))
            .border(1.dp, bgColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewQualityCheck() {
    WaterQualityTheme {
        QualityCheck(ph = 4.0f, tds = 100.0f, temp = 30.0f)
    }
}