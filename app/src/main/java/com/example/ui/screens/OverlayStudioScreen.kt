package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OverlayLayoutStyle
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSwitch
import com.example.ui.components.FloatingHudOverlayContent
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
import com.example.ui.theme.StealthViolet
import com.example.ui.viewmodel.GamingBoosterViewModel

@Composable
fun OverlayStudioScreen(
    viewModel: GamingBoosterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val overlayConfig by viewModel.overlayConfig.collectAsState()
    val stats by viewModel.performanceStats.collectAsState()

    var isTestAlertActive by remember { mutableStateOf(false) }
    val hasPermission = viewModel.hasOverlayPermission()

    val cyberColors = listOf(
        0xFF00FF9D to "Cyber Lime",
        0xFF00E5FF to "Electric Cyan",
        0xFFFF2A6D to "Neon Crimson",
        0xFFFFB800 to "Apex Amber",
        0xFF9D4EDD to "Stealth Violet"
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GAMING HUD STUDIO",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Customize real-time in-game HUD overlay & alerts",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (overlayConfig.isEnabled) CyberLime.copy(alpha = 0.2f) else DarkSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (overlayConfig.isEnabled) "HUD ACTIVE" else "HUD OFF",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (overlayConfig.isEnabled) CyberLime else Color.Gray
                    )
                }
            }
        }

        // Permission & Main Toggle Card
        item {
            CyberCard(
                borderColor = if (!hasPermission) NeonCrimson else if (overlayConfig.isEnabled) CyberLime else DarkBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ENABLE FLOATING HUD",
                            fontFamily = OrbitronFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (hasPermission) "Shows FPS, CPU, RAM and Temp over your games"
                            else "Requires 'Display over other apps' system permission",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            color = if (hasPermission) Color.LightGray else NeonCrimson
                        )
                    }

                    CyberSwitch(
                        checked = overlayConfig.isEnabled,
                        onCheckedChange = {
                            if (!hasPermission) {
                                requestOverlayPermission(context)
                            } else {
                                viewModel.toggleOverlayService()
                            }
                        },
                        accentColor = CyberLime,
                        testTag = "overlay_main_switch"
                    )
                }

                if (!hasPermission) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CyberButton(
                        text = "GRANT OVERLAY PERMISSION",
                        onClick = { requestOverlayPermission(context) },
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = true,
                        accentColor = NeonCrimson,
                        testTag = "grant_overlay_permission_button"
                    )
                }
            }
        }

        // Live Interactive Preview Simulator
        item {
            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE HUD SIMULATOR PREVIEW",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "REAL-TIME",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberLime
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Simulated Game Canvas Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF13192B),
                                    Color(0xFF0F2B48),
                                    Color(0xFF181028)
                                )
                            )
                        )
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Simulated in-game graphics text
                    Text(
                        text = "🎮 GAMEPLAY RENDER SURFACE [120 FPS]",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.25f)
                    )

                    // Rendered Real HUD View
                    FloatingHudOverlayContent(
                        statsFlow = viewModel.performanceStats,
                        configFlow = viewModel.overlayConfig,
                        onQuickBoost = { viewModel.boostNow() }
                    )
                }
            }
        }

        // Layout Style Picker
        item {
            CyberCard {
                Text(
                    text = "HUD LAYOUT STYLE",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OverlayLayoutStyle.values().forEach { style ->
                        val isSelected = overlayConfig.layoutStyle == style
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyberLime.copy(alpha = 0.12f) else DarkBg)
                                .border(
                                    1.dp,
                                    if (isSelected) CyberLime else DarkBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.updateOverlayConfig { it.copy(layoutStyle = style) }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = style.displayName,
                                    fontFamily = OrbitronFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CyberLime else Color.White
                                )
                                Text(
                                    text = style.description,
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = CyberLime,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // High Temperature Alert Configuration & Test
        item {
            CyberCard(
                borderColor = if (stats.isHighTempAlert) NeonCrimson else DarkBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = NeonCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "HIGH TEMPERATURE ALERT",
                            fontFamily = OrbitronFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "${overlayConfig.tempAlertThresholdC.toInt()}°C (${((overlayConfig.tempAlertThresholdC * 9 / 5) + 32).toInt()}°F)",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCrimson
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Alerts during gameplay if thermal limits are exceeded to avoid throttling and battery heat wear.",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 11.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Threshold Slider
                Slider(
                    value = overlayConfig.tempAlertThresholdC,
                    onValueChange = { newVal ->
                        viewModel.updateOverlayConfig { it.copy(tempAlertThresholdC = newVal) }
                    },
                    valueRange = 38f..48f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCrimson,
                        activeTrackColor = NeonCrimson,
                        inactiveTrackColor = DarkBorder
                    ),
                    modifier = Modifier.testTag("temp_threshold_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("38°C (Strict)", fontSize = 10.sp, color = Color.Gray, fontFamily = RajdhaniFontFamily)
                    Text("42°C (Optimal)", fontSize = 10.sp, color = CyberLime, fontFamily = RajdhaniFontFamily)
                    Text("48°C (Extreme)", fontSize = 10.sp, color = NeonCrimson, fontFamily = RajdhaniFontFamily)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Vibration & Sound Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Vibration Toggle
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBg)
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                            Text("Haptic Pulse", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = Color.White)
                        }
                        CyberSwitch(
                            checked = overlayConfig.isVibrationAlertEnabled,
                            onCheckedChange = { checked ->
                                viewModel.updateOverlayConfig { it.copy(isVibrationAlertEnabled = checked) }
                            },
                            accentColor = NeonCrimson,
                            testTag = "alert_vibrate_switch"
                        )
                    }

                    // Audio Toggle
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBg)
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                            Text("Audio Beep", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = Color.White)
                        }
                        CyberSwitch(
                            checked = overlayConfig.isAudioAlertEnabled,
                            onCheckedChange = { checked ->
                                viewModel.updateOverlayConfig { it.copy(isAudioAlertEnabled = checked) }
                            },
                            accentColor = NeonCrimson,
                            testTag = "alert_audio_switch"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Instant Test Alert Button
                CyberButton(
                    text = if (isTestAlertActive) "DISMISS TEST ALERT" else "TEST HIGH TEMP ALERT NOW",
                    onClick = {
                        isTestAlertActive = !isTestAlertActive
                        viewModel.triggerTestHighTempAlert(isTestAlertActive)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = !isTestAlertActive,
                    accentColor = NeonCrimson,
                    testTag = "test_high_temp_alert_button"
                )
            }
        }

        // Opacity & Accent Color Customizer
        item {
            CyberCard {
                Text(
                    text = "AESTHETICS & ACCENTS",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("HUD Opacity / Transparency", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = Color.LightGray)
                    Text("${(overlayConfig.opacity * 100).toInt()}%", fontFamily = OrbitronFontFamily, fontSize = 12.sp, color = CyberCyan)
                }

                Slider(
                    value = overlayConfig.opacity,
                    onValueChange = { newVal ->
                        viewModel.updateOverlayConfig { it.copy(opacity = newVal) }
                    },
                    valueRange = 0.35f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberCyan,
                        activeTrackColor = CyberCyan,
                        inactiveTrackColor = DarkBorder
                    ),
                    modifier = Modifier.testTag("overlay_opacity_slider")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Cyber Accent Color", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    cyberColors.forEach { (colorHex, name) ->
                        val color = Color(colorHex)
                        val isSelected = overlayConfig.accentColorHex == colorHex

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else DarkBorder,
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.updateOverlayConfig { it.copy(accentColorHex = colorHex) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = name,
                                    tint = DarkBg,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Metrics Visibility Toggles
        item {
            CyberCard {
                Text(
                    text = "METRICS VISIBILITY",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                MetricToggleRow("Frame Rate (FPS & 1% Low)", overlayConfig.showFps) {
                    viewModel.updateOverlayConfig { it.copy(showFps = !overlayConfig.showFps) }
                }
                MetricToggleRow("CPU Utilization Load (%)", overlayConfig.showCpu) {
                    viewModel.updateOverlayConfig { it.copy(showCpu = !overlayConfig.showCpu) }
                }
                MetricToggleRow("Memory Consumption (RAM)", overlayConfig.showRam) {
                    viewModel.updateOverlayConfig { it.copy(showRam = !overlayConfig.showRam) }
                }
                MetricToggleRow("Hardware Thermal & Temperature", overlayConfig.showTemp) {
                    viewModel.updateOverlayConfig { it.copy(showTemp = !overlayConfig.showTemp) }
                }
                MetricToggleRow("Network Ping Latency (ms)", overlayConfig.showPing) {
                    viewModel.updateOverlayConfig { it.copy(showPing = !overlayConfig.showPing) }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricToggleRow(
    title: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = RajdhaniFontFamily,
            fontSize = 13.sp,
            color = Color.LightGray
        )
        CyberSwitch(
            checked = checked,
            onCheckedChange = { onToggle() },
            accentColor = CyberCyan,
            testTag = "toggle_${title.take(5)}"
        )
    }
}

private fun requestOverlayPermission(context: Context) {
    try {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + context.packageName)
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val fallback = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallback)
    }
}
