package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppProcessItem
import com.example.model.AutoBoostConfig
import com.example.model.BoostResult
import com.example.model.GameItem
import com.example.model.GamingProfile
import com.example.model.OverlayConfig
import com.example.model.OverlayLayoutStyle
import com.example.model.PerformanceStats
import com.example.monitor.CpuRamMonitor
import com.example.monitor.FpsMonitor
import com.example.monitor.NetworkMonitor
import com.example.monitor.ProcessOptimizer
import com.example.monitor.ThermalBatteryMonitor
import com.example.monitor.VibrationHelper
import com.example.service.GamingOverlayService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GamingBoosterViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext

    val fpsMonitor = FpsMonitor(context)
    val cpuRamMonitor = CpuRamMonitor(context)
    val thermalMonitor = ThermalBatteryMonitor(context)
    val networkMonitor = NetworkMonitor(context)
    val processOptimizer = ProcessOptimizer(context)
    val vibrationHelper = VibrationHelper(context)

    private val _currentProfile = MutableStateFlow(GamingProfile.ULTRA)
    val currentProfile: StateFlow<GamingProfile> = _currentProfile.asStateFlow()

    private val _overlayConfig = MutableStateFlow(OverlayConfig())
    val overlayConfig: StateFlow<OverlayConfig> = _overlayConfig.asStateFlow()

    private val _autoBoostConfig = MutableStateFlow(AutoBoostConfig())
    val autoBoostConfig: StateFlow<AutoBoostConfig> = _autoBoostConfig.asStateFlow()

    private val _isBoosting = MutableStateFlow(false)
    val isBoosting: StateFlow<Boolean> = _isBoosting.asStateFlow()

    private val _lastBoostResult = MutableStateFlow<BoostResult?>(null)
    val lastBoostResult: StateFlow<BoostResult?> = _lastBoostResult.asStateFlow()

    private val _installedGames = MutableStateFlow<List<GameItem>>(emptyList())
    val installedGames: StateFlow<List<GameItem>> = _installedGames.asStateFlow()

    private val _fpsHistory = MutableStateFlow<List<Int>>(List(30) { 60 })
    val fpsHistory: StateFlow<List<Int>> = _fpsHistory.asStateFlow()

    private val _cpuHistory = MutableStateFlow<List<Int>>(List(30) { 25 })
    val cpuHistory: StateFlow<List<Int>> = _cpuHistory.asStateFlow()

    private val _isStressArenaActive = MutableStateFlow(false)
    val isStressArenaActive: StateFlow<Boolean> = _isStressArenaActive.asStateFlow()

    private val _stressParticleCount = MutableStateFlow(800)
    val stressParticleCount: StateFlow<Int> = _stressParticleCount.asStateFlow()

    val performanceStats: StateFlow<PerformanceStats> = combine(
        fpsMonitor.fps,
        fpsMonitor.onePercentLow,
        fpsMonitor.frameTimeMs,
        fpsMonitor.maxRefreshRate,
        cpuRamMonitor.cpuUsage,
        cpuRamMonitor.ramInfo,
        thermalMonitor.state,
        networkMonitor.pingMs
    ) { params ->
        val fps = params[0] as Int
        val onePercentLow = params[1] as Int
        val frameTimeMs = params[2] as Float
        val maxRefresh = params[3] as Int
        val cpu = params[4] as Int
        val ram = params[5] as com.example.monitor.RamInfo
        val thermal = params[6] as com.example.monitor.ThermalBatteryState
        val ping = params[7] as Int

        PerformanceStats(
            fps = fps,
            targetFps = _currentProfile.value.targetFps.coerceAtMost(maxRefresh),
            onePercentLowFps = onePercentLow,
            frameTimeMs = frameTimeMs,
            cpuUsagePercent = cpu,
            ramUsedMb = ram.usedMb,
            ramTotalMb = ram.totalMb,
            ramUsagePercent = ram.usagePercent,
            temperatureC = thermal.temperatureC,
            temperatureF = thermal.temperatureF,
            thermalLevel = thermal.thermalLevel,
            batteryPercent = thermal.batteryPercent,
            isCharging = thermal.isCharging,
            pingMs = ping,
            isHighTempAlert = thermal.isHighTempAlert,
            highTempThresholdC = _overlayConfig.value.tempAlertThresholdC
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PerformanceStats()
    )

    init {
        fpsMonitor.start()
        GamingOverlayService.onQuickBoostRequested = {
            boostNow()
        }

        startTelemetryLoops()
        loadInstalledGames()
    }

    private fun startTelemetryLoops() {
        // Telemetry sampling ticker
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val stressMult = if (_isStressArenaActive.value) {
                    1.0f + (_stressParticleCount.value / 1200f)
                } else 1.0f

                cpuRamMonitor.updateMetrics(stressMult)
                networkMonitor.checkPing()

                val currentStats = performanceStats.value
                GamingOverlayService.servicePerformanceStats.value = currentStats

                // Update rolling histories
                val newFpsList = (_fpsHistory.value.drop(1) + currentStats.fps).takeLast(30)
                _fpsHistory.value = newFpsList

                val newCpuList = (_cpuHistory.value.drop(1) + currentStats.cpuUsagePercent).takeLast(30)
                _cpuHistory.value = newCpuList

                // Check auto-boost rules
                checkAutoBoostTrigger(currentStats)
            }
        }
    }

    private fun checkAutoBoostTrigger(stats: PerformanceStats) {
        val cfg = _autoBoostConfig.value
        if (cfg.isAutoBoostEnabled && !_isBoosting.value) {
            if (stats.ramUsagePercent >= cfg.ramThresholdPercent || stats.cpuUsagePercent >= cfg.cpuThresholdPercent) {
                boostNow(isAuto = true)
            }
        }
    }

    fun selectProfile(profile: GamingProfile) {
        _currentProfile.value = profile
        vibrationHelper.playHapticClick()
    }

    fun updateOverlayConfig(transform: (OverlayConfig) -> OverlayConfig) {
        val newConfig = transform(_overlayConfig.value)
        _overlayConfig.value = newConfig
        GamingOverlayService.serviceOverlayConfig.value = newConfig
        thermalMonitor.setThreshold(newConfig.tempAlertThresholdC)
    }

    fun updateAutoBoostConfig(transform: (AutoBoostConfig) -> AutoBoostConfig) {
        _autoBoostConfig.value = transform(_autoBoostConfig.value)
    }

    fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun toggleOverlayService() {
        if (!hasOverlayPermission()) {
            return
        }
        val shouldEnable = !_overlayConfig.value.isEnabled
        updateOverlayConfig { it.copy(isEnabled = shouldEnable) }

        val intent = Intent(context, GamingOverlayService::class.java)
        if (shouldEnable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            context.stopService(intent)
        }
    }

    fun triggerTestHighTempAlert(active: Boolean) {
        thermalMonitor.triggerTestAlert(active)
        if (active) {
            vibrationHelper.playHighTempWarning(withSound = _overlayConfig.value.isAudioAlertEnabled)
        }
    }

    fun boostNow(isAuto: Boolean = false) {
        if (_isBoosting.value) return
        viewModelScope.launch {
            _isBoosting.value = true
            vibrationHelper.playBoostSuccess()
            val cpuBefore = performanceStats.value.cpuUsagePercent
            delay(900) // Visual turbo pulse animation
            val result = processOptimizer.performBoost(cpuBefore)
            _lastBoostResult.value = result
            cpuRamMonitor.updateMetrics(0.7f)
            delay(400)
            _isBoosting.value = false
        }
    }

    fun loadInstalledGames() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(launcherIntent, 0)
            val gamesList = mutableListOf<GameItem>()

            for (resolve in resolveInfos) {
                val pkg = resolve.activityInfo.packageName
                if (pkg != context.packageName) {
                    val appLabel = resolve.loadLabel(pm).toString()
                    val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        resolve.activityInfo.applicationInfo.category == ApplicationInfo.CATEGORY_GAME
                    } else false

                    gamesList.add(
                        GameItem(
                            packageName = pkg,
                            title = appLabel,
                            isInstalledOnDevice = true,
                            preferredProfile = GamingProfile.ULTRA
                        )
                    )
                }
            }

            // Always add a few default popular benchmark / gaming presets if none installed
            if (gamesList.isEmpty()) {
                gamesList.addAll(
                    listOf(
                        GameItem("com.example.arena3d", "Cyber Arena 3D [Benchmark]", false, 0L, 120, GamingProfile.ULTRA),
                        GameItem("com.example.speedrace", "Velocity Drift Apex", false, 0L, 90, GamingProfile.ULTRA),
                        GameItem("com.example.shadowstrike", "Shadow Strike: Cyber", false, 0L, 60, GamingProfile.BALANCED)
                    )
                )
            }

            _installedGames.value = gamesList
            processOptimizer.refreshRunningTasks()
        }
    }

    fun launchGame(game: GameItem) {
        viewModelScope.launch {
            // Auto boost prior to game launch
            boostNow()
            delay(600)

            if (game.isInstalledOnDevice) {
                try {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(game.packageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun setStressArenaActive(active: Boolean) {
        _isStressArenaActive.value = active
    }

    fun setStressParticleCount(count: Int) {
        _stressParticleCount.value = count
    }

    override fun onCleared() {
        super.onCleared()
        fpsMonitor.stop()
        thermalMonitor.unregister()
        vibrationHelper.release()
    }
}
