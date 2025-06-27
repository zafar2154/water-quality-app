package com.example.waterquality

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.ui.theme.BgBlue
import com.example.waterquality.ui.theme.BgGreen
import com.example.waterquality.ui.theme.BgRed
import com.example.waterquality.ui.theme.BgYellow
import com.example.waterquality.ui.theme.WaterQualityTheme

@Composable
fun CurrentData(
    icon: Painter,
    value: Float,
    desc: String
) {
    val backgroundColorPh = when {
        value in 6.5..9.0 -> BgGreen
        else -> BgRed
    }

    val backgroundColorTds = when {
        value < 600 -> BgGreen
        value in 600.0..1000.0 -> BgYellow
        else -> BgRed
    }

    val backgroundColorTemp = when {
        value < 21 -> BgBlue
        value in 21.0 .. 40.0 -> BgGreen
        else -> BgRed
    }

    val backgroundMap = mapOf(
        "pH" to backgroundColorPh,
        "TDS" to backgroundColorTds,
        "Temperature" to backgroundColorTemp
    )

    fun getBackground(desc: String): Color {
        return backgroundMap[desc] ?: Color.Gray
    }


    Column(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .width(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(getBackground(desc))
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(40.dp)
                .aspectRatio(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            Text(
                fontFamily = FontFamily(Font(R.font.roboto)),
                text = value.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                fontFamily = FontFamily(Font(R.font.roboto)),
                text = desc,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        VerticalDivider(thickness = 1.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrentData(viewModel: SensorViewModel = viewModel()) {
    val sensorData by viewModel.sensorData.collectAsState()
    WaterQualityTheme {
        CurrentData(icon = painterResource(R.drawable.screenshot_2025_06_12_232854_removebg_preview), value = 70.0f, desc = "Temperature")
    }
}