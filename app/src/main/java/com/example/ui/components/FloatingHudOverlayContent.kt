package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OverlayConfig
import com.example.model.OverlayLayoutStyle
import com.example.model.PerformanceStats
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FloatingHudOverlayContent(
    statsFlow: StateFlow<PerformanceStats>,
    configFlow: StateFlow<OverlayConfig>,
    onQuickBoost: () -> Unit = {},
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val stats by statsFlow.collectAsState()
    val config by configFlow.collectAsState()

    var isExpanded by remember { mutableStateOf(false) }

    val accentColor = Color(config.accentColorHex)
    val alertColor = NeonCrimson

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val borderColor = if (stats.isHighTempAlert) {
        alertColor.copy(alpha = pulseAlpha)
    } else {
        accentColor.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(16.dp))
            .background(
                DarkBg.copy(alpha = config.opacity)
            )
            .border(
                width = if (stats.isHighTempAlert) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)
            .testTag("floating_hud_overlay")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // High Temp Alert Banner
            if (stats.isHighTempAlert) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(alertColor.copy(alpha = 0.25f * pulseAlpha))
                        .border(1.dp, alertColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Thermal Warning",
                        tint = alertColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "OVERHEAT: ${String.format("%.1f", stats.temperatureC)}°C",
                        color = alertColor,
                        fontFamily = OrbitronFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            when (config.layoutStyle) {
                OverlayLayoutStyle.MINIMAL_BADGE -> {
                    MinimalBadgeLayout(stats = stats, accentColor = accentColor, onQuickBoost = onQuickBoost)
                }
                OverlayLayoutStyle.COMPACT_PILL -> {
                    CompactPillLayout(
                        stats = stats,
                        config = config,
                        accentColor = accentColor,
                        onQuickBoost = onQuickBoost
                    )
                }
                OverlayLayoutStyle.CYBER_GAUGE -> {
                    CyberGaugeLayout(
                        stats = stats,
                        config = config,
                        accentColor = accentColor,
                        onQuickBoost = onQuickBoost
                    )
                }
                OverlayLayoutStyle.EXPANDED_HUD -> {
                    ExpandedHudLayout(
                        stats = stats,
                        config = config,
                        accentColor = accentColor,
                        onQuickBoost = onQuickBoost,
                        onClose = onClose
                    )
                }
            }
        }
    }
}

@Composable
private fun MinimalBadgeLayout(
    stats: PerformanceStats,
    accentColor: Color,
    onQuickBoost: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = "${stats.fps}",
            fontFamily = OrbitronFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Text(
            text = "FPS",
            fontFamily = RajdhaniFontFamily,
            fontSize = 11.sp,
            color = Color.LightGray
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.2f))
                .clickable { onQuickBoost() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Quick Boost",
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CompactPillLayout(
    stats: PerformanceStats,
    config: OverlayConfig,
    accentColor: Color,
    onQuickBoost: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        if (config.showFps) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stats.fps}",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text("FPS", fontSize = 9.sp, fontFamily = RajdhaniFontFamily, color = Color.Gray)
            }
        }

        if (config.showCpu) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stats.cpuUsagePercent}%",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text("CPU", fontSize = 9.sp, fontFamily = RajdhaniFontFamily, color = Color.Gray)
            }
        }

        if (config.showTemp) {
            val tempColor = if (stats.isHighTempAlert) NeonCrimson else Color(0xFFFFB800)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stats.temperatureC.toInt()}°C",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tempColor
                )
                Text("TEMP", fontSize = 9.sp, fontFamily = RajdhaniFontFamily, color = Color.Gray)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.2f))
                .clickable { onQuickBoost() }
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Quick Boost",
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun CyberGaugeLayout(
    stats: PerformanceStats,
    config: OverlayConfig,
    accentColor: Color,
    onQuickBoost: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(DarkSurface)
                .border(
                    2.dp,
                    Brush.sweepGradient(
                        listOf(accentColor, Color(0xFF00E5FF), accentColor)
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stats.fps}",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = "FPS",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stats.temperatureC.toInt()}°C",
                fontSize = 10.sp,
                fontFamily = OrbitronFontFamily,
                color = if (stats.isHighTempAlert) NeonCrimson else Color(0xFFFFB800)
            )
            Text(
                text = "•",
                fontSize = 10.sp,
                color = Color.DarkGray
            )
            Text(
                text = "CPU ${stats.cpuUsagePercent}%",
                fontSize = 10.sp,
                fontFamily = OrbitronFontFamily,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ExpandedHudLayout(
    stats: PerformanceStats,
    config: OverlayConfig,
    accentColor: Color,
    onQuickBoost: () -> Unit,
    onClose: (() -> Unit)?
) {
    Column(
        modifier = Modifier.width(260.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Header with Drag Handle & Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Drag overlay",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "APEX HUD",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .clickable { onQuickBoost() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Boost",
                            tint = accentColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "BOOST",
                            fontFamily = OrbitronFontFamily,
                            fontSize = 9.sp,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (onClose != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close HUD",
                            tint = Color.LightGray,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (config.showFps) {
                MetricItem(
                    label = "FPS",
                    value = "${stats.fps}",
                    unit = "1% Low: ${stats.onePercentLowFps}",
                    valueColor = accentColor
                )
            }
            if (config.showCpu) {
                MetricItem(
                    label = "CPU",
                    value = "${stats.cpuUsagePercent}%",
                    unit = "Load",
                    valueColor = Color.White
                )
            }
            if (config.showRam) {
                MetricItem(
                    label = "RAM",
                    value = "${stats.ramUsagePercent}%",
                    unit = "${stats.ramUsedMb}M",
                    valueColor = Color(0xFF00E5FF)
                )
            }
            if (config.showTemp) {
                val tempColor = if (stats.isHighTempAlert) NeonCrimson else Color(0xFFFFB800)
                MetricItem(
                    label = "TEMP",
                    value = "${String.format("%.1f", stats.temperatureC)}°",
                    unit = stats.thermalLevel.label.take(6),
                    valueColor = tempColor
                )
            }
            if (config.showPing) {
                MetricItem(
                    label = "PING",
                    value = "${stats.pingMs}",
                    unit = "ms",
                    valueColor = if (stats.pingMs < 50) accentColor else Color(0xFFFFB800)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    unit: String,
    valueColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontFamily = RajdhaniFontFamily,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
        Text(
            text = value,
            fontFamily = OrbitronFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = unit,
            fontFamily = RajdhaniFontFamily,
            fontSize = 8.sp,
            color = Color.LightGray
        )
    }
}
