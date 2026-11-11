package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GamingProfile
import com.example.model.ThermalLevel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSwitch
import com.example.ui.components.CyberTurbometer
import com.example.ui.components.LivePerformanceGraph
import com.example.ui.components.StatMiniCard
import com.example.ui.theme.ApexAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberLime
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.viewmodel.GamingBoosterViewModel

@Composable
fun DashboardScreen(
    viewModel: GamingBoosterViewModel,
    onNavigateToOverlayStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.performanceStats.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()
    val isBoosting by viewModel.isBoosting.collectAsState()
    val lastResult by viewModel.lastBoostResult.collectAsState()
    val fpsHistory by viewModel.fpsHistory.collectAsState()
    val cpuHistory by viewModel.cpuHistory.collectAsState()
    val overlayConfig by viewModel.overlayConfig.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulseAlert")
    val alertPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alertAlpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // App Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(CyberLime)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "APEX GAMING BOOSTER",
                            fontFamily = OrbitronFontFamily,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "TURBO ENGINE ACTIVE • ${stats.targetFps}Hz TARGET",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberLime
                    )
                }

                // Profile Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberLime.copy(alpha = 0.15f))
                        .border(1.dp, CyberLime, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentProfile.title.take(5),
                        fontFamily = OrbitronFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberLime
                    )
                }
            }
        }

        // High Temp Urgent Alert Warning Card if active
        if (stats.isHighTempAlert) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NeonCrimson.copy(alpha = 0.18f * alertPulse))
                        .border(1.5.dp, NeonCrimson.copy(alpha = alertPulse), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                        .testTag("high_temp_alert_banner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonCrimson.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = NeonCrimson,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CRITICAL HIGH TEMPERATURE!",
                                fontFamily = OrbitronFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCrimson
                            )
                            Text(
                                text = "Device is ${String.format("%.1f", stats.temperatureC)}°C (Threshold: ${stats.highTempThresholdC.toInt()}°C). Thermal throttling imminent.",
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Central Cyber Turbometer Dial
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                CyberTurbometer(
                    stats = stats,
                    onDialClick = { viewModel.boostNow() }
                )
            }
        }

        // Quick Boost Action Button
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CyberButton(
                    text = if (isBoosting) "OPTIMIZING ENGINE..." else "⚡ ULTRA BOOST NOW",
                    onClick = { viewModel.boostNow() },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    accentColor = CyberLime,
                    testTag = "ultra_boost_button"
                )

                AnimatedVisibility(
                    visible = lastResult != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (lastResult != null) {
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyberLime,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Boost Success: +${lastResult!!.ramFreedMb}MB RAM freed (${lastResult!!.killedProcessesCount} tasks closed)",
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }

        // Live 4-Metric Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMiniCard(
                    title = "TARGET FPS",
                    value = "${stats.fps} / ${stats.targetFps}",
                    subtitle = "Frame Time ${stats.frameTimeMs}ms",
                    icon = Icons.Default.Speed,
                    iconColor = CyberLime,
                    modifier = Modifier.weight(1f)
                )

                StatMiniCard(
                    title = "TEMPERATURE",
                    value = "${String.format("%.1f", stats.temperatureC)}°C",
                    subtitle = stats.thermalLevel.label,
                    icon = Icons.Default.DeviceThermostat,
                    iconColor = if (stats.isHighTempAlert) NeonCrimson else ApexAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMiniCard(
                    title = "RAM CONSUMED",
                    value = "${stats.ramUsagePercent}%",
                    subtitle = "${stats.ramUsedMb}MB / ${stats.ramTotalMb}MB",
                    icon = Icons.Default.Bolt,
                    iconColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )

                StatMiniCard(
                    title = "PING LATENCY",
                    value = "${stats.pingMs} ms",
                    subtitle = if (stats.pingMs < 45) "Ultra Stable" else "Standard",
                    icon = Icons.Default.Wifi,
                    iconColor = if (stats.pingMs < 45) CyberLime else ApexAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Floating Overlay Quick Control Card
        item {
            CyberCard(
                borderColor = if (overlayConfig.isEnabled) CyberLime else DarkBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberLime.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = CyberLime,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "FLOATING GAMING HUD",
                                fontFamily = OrbitronFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (overlayConfig.isEnabled) "Overlay active over games" else "Real-time FPS & high-temp alerts",
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 12.sp,
                                color = if (overlayConfig.isEnabled) CyberLime else Color.Gray
                            )
                        }
                    }

                    CyberSwitch(
                        checked = overlayConfig.isEnabled,
                        onCheckedChange = { viewModel.toggleOverlayService() },
                        accentColor = CyberLime,
                        testTag = "dashboard_overlay_switch"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .clickable { onNavigateToOverlayStudio() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customize HUD Style, Opacity & Alerts",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberCyan
                    )
                    Text(
                        text = "SETTINGS →",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }
        }

        // Live Telemetry Waveform Graph
        item {
            LivePerformanceGraph(
                fpsHistory = fpsHistory,
                cpuHistory = cpuHistory,
                maxFps = stats.targetFps
            )
        }

        // Gaming Profiles Selector
        item {
            CyberCard {
                Text(
                    text = "PERFORMANCE TUNING PROFILE",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GamingProfile.values().forEach { profile ->
                        val isSelected = currentProfile == profile
                        val profileColor = when (profile) {
                            GamingProfile.ULTRA -> CyberLime
                            GamingProfile.BALANCED -> CyberCyan
                            GamingProfile.ECO -> Color(0xFF7C4DFF)
                            GamingProfile.CUSTOM -> ApexAmber
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) profileColor.copy(alpha = 0.15f) else DarkBg)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) profileColor else DarkBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.selectProfile(profile) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.title,
                                    fontFamily = OrbitronFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) profileColor else Color.White
                                )
                                Text(
                                    text = profile.description,
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = profile.cpuBoostLevel,
                                    fontFamily = OrbitronFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = profileColor
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
