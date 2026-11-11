package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppProcessItem
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSwitch
import com.example.ui.theme.ApexAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberLime
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.viewmodel.GamingBoosterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OptimizerScreen(
    viewModel: GamingBoosterViewModel,
    modifier: Modifier = Modifier
) {
    val autoBoostConfig by viewModel.autoBoostConfig.collectAsState()
    val runningApps by viewModel.processOptimizer.runningApps.collectAsState()
    val whitelist by viewModel.processOptimizer.whitelist.collectAsState()
    val boostHistory by viewModel.processOptimizer.boostHistory.collectAsState()
    val stats by viewModel.performanceStats.collectAsState()
    val isBoosting by viewModel.isBoosting.collectAsState()

    val totalConsumingMb = runningApps.sumOf { it.memoryUsageMb }

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
                        text = "TASK OPTIMIZER",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Automatic background cleaner & memory reclamation",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .clickable { viewModel.boostNow() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Quick boost",
                        tint = CyberLime,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Automatic Background Cleaner Engine Card
        item {
            CyberCard(
                borderColor = if (autoBoostConfig.isAutoBoostEnabled) CyberLime else DarkBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberLime.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoMode,
                                contentDescription = null,
                                tint = CyberLime,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "AUTOMATIC BACKGROUND CLEANER",
                                fontFamily = OrbitronFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (autoBoostConfig.isAutoBoostEnabled) "Active • Trims RAM when thresholds exceed" else "Disabled",
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 11.sp,
                                color = if (autoBoostConfig.isAutoBoostEnabled) CyberLime else Color.Gray
                            )
                        }
                    }

                    CyberSwitch(
                        checked = autoBoostConfig.isAutoBoostEnabled,
                        onCheckedChange = { checked ->
                            viewModel.updateAutoBoostConfig { it.copy(isAutoBoostEnabled = checked) }
                        },
                        accentColor = CyberLime,
                        testTag = "auto_boost_switch"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // RAM Threshold Trigger
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Auto-Clean RAM Threshold", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = Color.LightGray)
                    Text("${autoBoostConfig.ramThresholdPercent}%", fontFamily = OrbitronFontFamily, fontSize = 12.sp, color = CyberCyan)
                }

                Slider(
                    value = autoBoostConfig.ramThresholdPercent.toFloat(),
                    onValueChange = { newVal ->
                        viewModel.updateAutoBoostConfig { it.copy(ramThresholdPercent = newVal.toInt()) }
                    },
                    valueRange = 60f..95f,
                    steps = 6,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberCyan,
                        activeTrackColor = CyberCyan,
                        inactiveTrackColor = DarkBorder
                    ),
                    modifier = Modifier.testTag("ram_threshold_slider")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // CPU Threshold Trigger
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Auto-Clean CPU Spike Threshold", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = Color.LightGray)
                    Text("${autoBoostConfig.cpuThresholdPercent}%", fontFamily = OrbitronFontFamily, fontSize = 12.sp, color = ApexAmber)
                }

                Slider(
                    value = autoBoostConfig.cpuThresholdPercent.toFloat(),
                    onValueChange = { newVal ->
                        viewModel.updateAutoBoostConfig { it.copy(cpuThresholdPercent = newVal.toInt()) }
                    },
                    valueRange = 70f..95f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = ApexAmber,
                        activeTrackColor = ApexAmber,
                        inactiveTrackColor = DarkBorder
                    ),
                    modifier = Modifier.testTag("cpu_threshold_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Auto-Boost on Game Launch Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Auto-Boost When Any Game Boots",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    CyberSwitch(
                        checked = autoBoostConfig.boostOnGameLaunch,
                        onCheckedChange = { checked ->
                            viewModel.updateAutoBoostConfig { it.copy(boostOnGameLaunch = checked) }
                        },
                        accentColor = CyberLime,
                        testTag = "auto_boost_launch_switch"
                    )
                }
            }
        }

        // Running Background Tasks Card & Cleaner Button
        item {
            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RUNNING BACKGROUND PROCESSES",
                            fontFamily = OrbitronFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${runningApps.size} detected tasks • Consuming ~${totalConsumingMb}MB",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }

                    Text(
                        text = "${stats.ramUsagePercent}% RAM",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CyberButton(
                    text = if (isBoosting) "RECLAIMING MEMORY..." else "KILL UNPROTECTED TASKS",
                    onClick = { viewModel.boostNow() },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    accentColor = CyberLime,
                    testTag = "clear_tasks_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Running App Items
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    runningApps.take(8).forEach { app ->
                        val isWhitelisted = whitelist.contains(app.packageName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkBg)
                                .border(1.dp, if (isWhitelisted) CyberCyan.copy(alpha = 0.5f) else DarkBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(if (isWhitelisted) CyberCyan.copy(alpha = 0.15f) else DarkSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isWhitelisted) Icons.Default.Lock else Icons.Default.Memory,
                                        contentDescription = null,
                                        tint = if (isWhitelisted) CyberCyan else Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = app.appName,
                                        fontFamily = RajdhaniFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${app.memoryUsageMb} MB ${if (isWhitelisted) "• [Protected / Whitelisted]" else ""}",
                                        fontFamily = RajdhaniFontFamily,
                                        fontSize = 10.sp,
                                        color = if (isWhitelisted) CyberCyan else Color.Gray
                                    )
                                }
                            }

                            // Toggle Whitelist Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isWhitelisted) CyberCyan.copy(alpha = 0.2f) else DarkSurfaceVariant)
                                    .clickable {
                                        viewModel.processOptimizer.toggleWhitelist(app.packageName)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isWhitelisted) "UNPROTECT" else "WHITELIST",
                                    fontFamily = OrbitronFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWhitelisted) CyberCyan else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Boost History Log
        item {
            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BOOST HISTORY LOG",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = CyberLime,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (boostHistory.isEmpty()) {
                    Text(
                        text = "No boosts recorded yet. Tap 'Ultra Boost' or launch a game to optimize memory.",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                } else {
                    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        boostHistory.take(5).forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkBg)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "+${log.ramFreedMb}MB Reclaimed",
                                        fontFamily = OrbitronFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberLime
                                    )
                                    Text(
                                        text = "${log.killedProcessesCount} tasks terminated • CPU ${log.cpuLoadBefore}% → ${log.cpuLoadAfter}%",
                                        fontFamily = RajdhaniFontFamily,
                                        fontSize = 11.sp,
                                        color = Color.LightGray
                                    )
                                }

                                Text(
                                    text = dateFormat.format(Date(log.timestamp)),
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 11.sp,
                                    color = Color.Gray
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
