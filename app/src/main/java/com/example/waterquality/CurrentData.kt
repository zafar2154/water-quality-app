package com.example.waterquality

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.example.waterquality.ui.theme.BgBlue
import com.example.waterquality.ui.theme.BgGreen
import com.example.waterquality.ui.theme.BgRed
import com.example.waterquality.ui.theme.BgYellow
import com.example.waterquality.ui.theme.WaterQualityTheme

enum class SensorType {
    pH, TDS, Temperature
}
fun getBackgroundColor(type: SensorType, value: Float): Color = when (type) {
    SensorType.pH -> when {
        value in 6.5f..9.0f -> BgGreen
        else                 -> BgRed
    }
    SensorType.TDS -> when {
        value < 600f        -> BgGreen
        value in 600f..1000f-> BgYellow
        else                -> BgRed
    }
    SensorType.Temperature -> when {
        value < 21f         -> BgBlue
        value in 21f..40f   -> BgGreen
        else                -> BgRed
    }
}

@Composable
fun CurrentData(
    icon: Painter,
    value: Float?,
    desc: SensorType
) {
    val bgColor = value?.let { getBackgroundColor(desc, it) } ?: Color.LightGray

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
fun PreviewCurrentData(viewModel: SensorViewModel = viewModel()) {
    WaterQualityTheme {
        CurrentData(icon = painterResource(R.drawable.premium_vector___water_drop_logo_images_illustration_design_removebg_preview), value = null, desc = SensorType.pH)
    }
}