package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameItem
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
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
import kotlin.random.Random

private class StressParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val radius: Float
)

@Composable
fun GameSpaceScreen(
    viewModel: GamingBoosterViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.installedGames.collectAsState()
    val isStressActive by viewModel.isStressArenaActive.collectAsState()
    val particleCount by viewModel.stressParticleCount.collectAsState()
    val stats by viewModel.performanceStats.collectAsState()

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
                        text = "GAME SPACE VAULT",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Auto-optimized launch & live FPS stress arena",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = CyberLime
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .clickable { viewModel.loadInstalledGames() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh games",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 3D Physics & Particle Stress Arena Card
        item {
            CyberCard(
                borderColor = if (isStressActive) NeonCrimson else DarkBorder
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
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = if (isStressActive) NeonCrimson else CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "3D PARTICLE STRESS ARENA",
                            fontFamily = OrbitronFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = if (isStressActive) "STRESS ACTIVE" else "STANDBY",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isStressActive) NeonCrimson else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Runs real physics particle collisions to stress GPU/CPU, test frame rate stability, and trigger thermal alert behavior.",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 11.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Physics Simulation Canvas
                StressSimulationCanvas(
                    isActive = isStressActive,
                    targetParticleCount = particleCount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, if (isStressActive) NeonCrimson else DarkBorder, RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Particle Count Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Particle Load: $particleCount", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = Color.White)
                    Text("Live FPS: ${stats.fps}", fontFamily = OrbitronFontFamily, fontSize = 12.sp, color = CyberLime)
                }

                Slider(
                    value = particleCount.toFloat(),
                    onValueChange = { viewModel.setStressParticleCount(it.toInt()) },
                    valueRange = 200f..3500f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberCyan,
                        activeTrackColor = CyberCyan,
                        inactiveTrackColor = DarkBorder
                    ),
                    modifier = Modifier.testTag("particle_stress_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                CyberButton(
                    text = if (isStressActive) "STOP STRESS TEST" else "🔥 LAUNCH BENCHMARK STRESS",
                    onClick = { viewModel.setStressArenaActive(!isStressActive) },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = !isStressActive,
                    accentColor = if (isStressActive) NeonCrimson else CyberCyan,
                    testTag = "stress_test_toggle_button"
                )
            }
        }

        // Installed Games Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MY GAMES & APPS (${games.size})",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "AUTO-BOOST ENABLED",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyberLime
                )
            }
        }

        // List of Games in Vault
        items(games, key = { it.packageName }) { game ->
            GameItemCard(
                game = game,
                onBoostAndLaunch = { viewModel.launchGame(game) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StressSimulationCanvas(
    isActive: Boolean,
    targetParticleCount: Int,
    modifier: Modifier = Modifier
) {
    var gravityCenter by remember { mutableStateOf<Offset?>(null) }
    val particles = remember { mutableStateListOf<StressParticle>() }

    LaunchedEffect(targetParticleCount) {
        val current = particles.size
        if (current < targetParticleCount) {
            val colors = listOf(CyberLime, CyberCyan, NeonCrimson, ApexAmber, Color(0xFF7C4DFF))
            for (i in current until targetParticleCount) {
                particles.add(
                    StressParticle(
                        x = Random.nextFloat() * 800f,
                        y = Random.nextFloat() * 400f,
                        vx = (Random.nextFloat() - 0.5f) * 8f,
                        vy = (Random.nextFloat() - 0.5f) * 8f,
                        color = colors[Random.nextInt(colors.size)],
                        radius = 2.5f + Random.nextFloat() * 3f
                    )
                )
            }
        } else if (current > targetParticleCount) {
            while (particles.size > targetParticleCount) {
                particles.removeAt(particles.size - 1)
            }
        }
    }

    // Animation frame ticker for particle physics
    var frameTick by remember { mutableStateOf(0L) }
    LaunchedEffect(isActive) {
        while (isActive) {
            withFrameNanos { time ->
                frameTick = time
            }
        }
    }

    Box(
        modifier = modifier
            .background(DarkBg)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> gravityCenter = offset },
                    onDragEnd = { gravityCenter = null },
                    onDragCancel = { gravityCenter = null },
                    onDrag = { change, _ ->
                        gravityCenter = change.position
                        change.consume()
                    }
                )
            }
            .testTag("stress_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val gPoint = gravityCenter

            for (p in particles) {
                if (isActive) {
                    if (gPoint != null) {
                        val dx = gPoint.x - p.x
                        val dy = gPoint.y - p.y
                        val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat().coerceAtLeast(10f)
                        val force = 280f / (dist)
                        p.vx += (dx / dist) * force * 0.1f
                        p.vy += (dy / dist) * force * 0.1f
                    }

                    p.x += p.vx
                    p.y += p.vy

                    // Wall bounce
                    if (p.x < 0) { p.x = 0f; p.vx = -p.vx }
                    if (p.x > w) { p.x = w; p.vx = -p.vx }
                    if (p.y < 0) { p.y = 0f; p.vy = -p.vy }
                    if (p.y > h) { p.y = h; p.vy = -p.vy }
                }

                drawCircle(
                    color = p.color,
                    radius = p.radius,
                    center = Offset(p.x.coerceIn(0f, w), p.y.coerceIn(0f, h))
                )
            }
        }

        if (!isActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TAP 'LAUNCH BENCHMARK' TO STRESS TEST",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            }
        }
    }
}

@Composable
private fun GameItemCard(
    game: GameItem,
    onBoostAndLaunch: () -> Unit
) {
    CyberCard(
        borderColor = DarkBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyberCyan.copy(alpha = 0.2f), CyberLime.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, CyberLime.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = CyberLime,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = game.title,
                        fontFamily = OrbitronFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${game.targetFps} FPS",
                                fontFamily = OrbitronFontFamily,
                                fontSize = 9.sp,
                                color = CyberCyan
                            )
                        }
                        Text(
                            text = game.preferredProfile.title,
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }

            CyberButton(
                text = "BOOST & PLAY",
                onClick = onBoostAndLaunch,
                icon = Icons.Default.Bolt,
                isPrimary = true,
                accentColor = CyberLime,
                testTag = "boost_play_${game.title.take(4)}"
            )
        }
    }
}
