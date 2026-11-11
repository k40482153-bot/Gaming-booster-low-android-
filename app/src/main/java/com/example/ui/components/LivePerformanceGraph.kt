package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberLime
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily

@Composable
fun LivePerformanceGraph(
    fpsHistory: List<Int>,
    cpuHistory: List<Int>,
    maxFps: Int = 120,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FRAME RATE & CPU TELEMETRY",
                fontFamily = OrbitronFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendIndicator(color = CyberLime, label = "FPS")
                LegendIndicator(color = CyberCyan, label = "CPU %")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height

                // Draw Horizontal Grid lines
                for (i in 1..3) {
                    val y = h * (i / 4f)
                    drawLine(
                        color = DarkBorder.copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Render FPS line
                if (fpsHistory.size > 1) {
                    val step = w / (fpsHistory.size - 1).coerceAtLeast(1)
                    val fpsPath = Path()
                    val fpsFillPath = Path()

                    fpsHistory.forEachIndexed { index, fpsValue ->
                        val norm = (fpsValue.toFloat() / maxFps.toFloat()).coerceIn(0f, 1f)
                        val x = index * step
                        val y = h - (norm * (h - 8.dp.toPx())) - 4.dp.toPx()

                        if (index == 0) {
                            fpsPath.moveTo(x, y)
                            fpsFillPath.moveTo(x, h)
                            fpsFillPath.lineTo(x, y)
                        } else {
                            fpsPath.lineTo(x, y)
                            fpsFillPath.lineTo(x, y)
                        }
                    }

                    fpsFillPath.lineTo(w, h)
                    fpsFillPath.close()

                    drawPath(
                        path = fpsFillPath,
                        brush = Brush.verticalGradient(
                            listOf(CyberLime.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )

                    drawPath(
                        path = fpsPath,
                        color = CyberLime,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Render CPU line
                if (cpuHistory.size > 1) {
                    val step = w / (cpuHistory.size - 1).coerceAtLeast(1)
                    val cpuPath = Path()

                    cpuHistory.forEachIndexed { index, cpuValue ->
                        val norm = (cpuValue.toFloat() / 100f).coerceIn(0f, 1f)
                        val x = index * step
                        val y = h - (norm * (h - 8.dp.toPx())) - 4.dp.toPx()

                        if (index == 0) {
                            cpuPath.moveTo(x, y)
                        } else {
                            cpuPath.lineTo(x, y)
                        }
                    }

                    drawPath(
                        path = cpuPath,
                        color = CyberCyan,
                        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendIndicator(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontFamily = RajdhaniFontFamily,
            fontSize = 11.sp,
            color = Color.LightGray
        )
    }
}
