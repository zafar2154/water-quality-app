package com.example.waterquality.ui.component

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
import com.example.waterquality.utils.QualityWaterCheck

@Composable
fun QualityCheck(ph: Float?, tds: Float?, temp: Float?) {
    val status = QualityWaterCheck.evaluate(ph, tds, temp)
    val bgColor = status.composeColor

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
            text = status.label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewQualityCheck() {
    WaterQualityTheme {
        QualityCheck(ph = 6.0f, tds = 60.0f, temp = 30.0f)
    }
}