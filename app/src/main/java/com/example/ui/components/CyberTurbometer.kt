package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PerformanceStats
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberLime
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily

@Composable
fun CyberTurbometer(
    stats: PerformanceStats,
    onDialClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val animatedCpu by animateFloatAsState(
        targetValue = stats.cpuUsagePercent / 100f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "cpuAnim"
    )

    val animatedRam by animateFloatAsState(
        targetValue = stats.ramUsagePercent / 100f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "ramAnim"
    )

    val tempColor = if (stats.isHighTempAlert) NeonCrimson else Color(0xFFFFB800)

    Box(
        modifier = modifier
            .size(240.dp)
            .clickable { onDialClick() }
            .testTag("cyber_turbometer_dial"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(230.dp)) {
            val strokeOuter = 12.dp.toPx()
            val strokeInner = 8.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer Track (CPU Gauge)
            val outerRadius = (size.width - strokeOuter) / 2f
            drawArc(
                color = DarkBorder.copy(alpha = 0.5f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
            )

            // Outer Active Arc (CPU Usage)
            val cpuSweep = 270f * animatedCpu.coerceIn(0f, 1f)
            val cpuColor = if (stats.cpuUsagePercent > 80) NeonCrimson else CyberLime
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(CyberLime, CyberCyan, cpuColor)
                ),
                startAngle = 135f,
                sweepAngle = cpuSweep,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
            )

            // Inner Track (RAM Gauge)
            val innerRadius = outerRadius - 16.dp.toPx()
            drawArc(
                color = DarkBorder.copy(alpha = 0.35f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = Stroke(width = strokeInner, cap = StrokeCap.Round)
            )

            // Inner Active Arc (RAM Usage)
            val ramSweep = 270f * animatedRam.coerceIn(0f, 1f)
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(CyberCyan, Color(0xFF7C4DFF), CyberCyan)
                ),
                startAngle = 135f,
                sweepAngle = ramSweep,
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = Stroke(width = strokeInner, cap = StrokeCap.Round)
            )

            // Tick Marks around perimeter
            val tickCount = 18
            for (i in 0..tickCount) {
                val angleDeg = 135f + (270f / tickCount) * i
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val r1 = outerRadius + 8.dp.toPx()
                val r2 = outerRadius + 13.dp.toPx()
                val p1 = Offset(
                    (center.x + r1 * Math.cos(angleRad)).toFloat(),
                    (center.y + r1 * Math.sin(angleRad)).toFloat()
                )
                val p2 = Offset(
                    (center.x + r2 * Math.cos(angleRad)).toFloat(),
                    (center.y + r2 * Math.sin(angleRad)).toFloat()
                )
                val isHighlighted = (i.toFloat() / tickCount) <= animatedCpu
                drawLine(
                    color = if (isHighlighted) cpuColor else DarkBorder,
                    start = p1,
                    end = p2,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${stats.fps}",
                fontFamily = OrbitronFontFamily,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = CyberLime
            )
            Text(
                text = "FPS REAL-TIME",
                fontFamily = RajdhaniFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.LightGray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${String.format("%.1f", stats.temperatureC)}°C • ${stats.cpuUsagePercent}% CPU",
                fontFamily = OrbitronFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = tempColor
            )
            Text(
                text = "RAM ${stats.ramUsagePercent}% (${stats.ramUsedMb}MB)",
                fontFamily = RajdhaniFontFamily,
                fontSize = 11.sp,
                color = CyberCyan
            )
        }
    }
}
