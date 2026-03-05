package com.example.waterquality.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.R
import com.example.waterquality.data.model.SensorType
import com.example.waterquality.ui.screen.homepage.SensorViewModel
import com.example.waterquality.ui.theme.BgBlue
import com.example.waterquality.ui.theme.BgGreen
import com.example.waterquality.ui.theme.BgRed
import com.example.waterquality.ui.theme.BgYellow
import com.example.waterquality.ui.theme.WaterQualityTheme
import com.example.waterquality.utils.QualityWaterCheck



@Composable
fun CurrentData(
    icon: Painter,
    value: Float?,
    desc: SensorType
) {
    val status = QualityWaterCheck.evaluateSensor(desc, value)
    val bgColor = status.composeColor

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Gambar ditempatkan dulu, dengan posisi offset ke atas
        Image(
            painter = icon,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(60.dp)
                .zIndex(1f)           // Pastikan gambar di atas
        )

        // Box dengan border dan isi teks
        Column(
            modifier = Modifier
                .padding(top = 25.dp) // Tambahkan padding agar teks tidak ketabrak gambar
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor.copy(alpha = 0.4f))
                .border(2.dp, bgColor, RoundedCornerShape(16.dp))
                .width(100.dp)
                .height(100.dp)
                .zIndex(0f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp)) // Spacer untuk kasih jarak dari gambar
            Text(
                fontFamily = FontFamily(Font(R.font.roboto)),
                text = value?.let {
                    if (it.isNaN()) "..." else "%.2f".format(it)
                } ?: "...",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                fontFamily = FontFamily(Font(R.font.roboto)),
                text = desc.toString(),
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewCurrentData() {
    WaterQualityTheme {
        CurrentData(icon = painterResource(R.drawable.logo_tds), value = 7.0f, desc = SensorType.PH)
    }
}