package com.example.model

enum class ThermalLevel(val label: String) {
    COOL("Cool - Optimal"),
    NORMAL("Normal"),
    WARM("Warm - Moderate"),
    HOT("High - Throttling Risk"),
    CRITICAL("Critical - Danger"),
    EMERGENCY("Emergency - Throttling Active")
}

data class PerformanceStats(
    val fps: Int = 60,
    val targetFps: Int = 60,
    val onePercentLowFps: Int = 55,
    val frameTimeMs: Float = 16.6f,
    val cpuUsagePercent: Int = 24,
    val ramUsedMb: Long = 2800L,
    val ramTotalMb: Long = 6144L,
    val ramUsagePercent: Int = 45,
    val temperatureC: Float = 36.5f,
    val temperatureF: Float = 97.7f,
    val thermalLevel: ThermalLevel = ThermalLevel.NORMAL,
    val batteryPercent: Int = 85,
    val isCharging: Boolean = false,
    val pingMs: Int = 28,
    val isHighTempAlert: Boolean = false,
    val highTempThresholdC: Float = 42.0f
)

enum class GamingProfile(
    val title: String,
    val subtitle: String,
    val description: String,
    val cpuBoostLevel: String,
    val targetFps: Int,
    val recommendedFor: String
) {
    ULTRA(
        title = "ULTRA GAMING",
        subtitle = "Maximum Performance",
        description = "Unlocks maximum CPU responsiveness, aggressive background task clearing, and frame rate stability.",
        cpuBoostLevel = "MAX 100%",
        targetFps = 120,
        recommendedFor = "Heavy 3D games, competitive shooters"
    ),
    BALANCED(
        title = "BALANCED PRO",
        subtitle = "Optimal Thermal Balance",
        description = "Dynamically adjusts performance to maintain smooth FPS while keeping device cool and preserving battery.",
        cpuBoostLevel = "DYNAMIC 75%",
        targetFps = 60,
        recommendedFor = "Standard gaming, casual titles, RPGs"
    ),
    ECO(
        title = "BATTERY SAVER",
        subtitle = "Long Gameplay Sessions",
        description = "Throttles background tasks and prioritizes power efficiency to extend play time.",
        cpuBoostLevel = "POWER SAVE 50%",
        targetFps = 60,
        recommendedFor = "Turn-based games, low battery emergencies"
    ),
    CUSTOM(
        title = "CUSTOM TUNING",
        subtitle = "User-Configured Engine",
        description = "Personalized settings for thermal threshold, target refresh rate, and auto-boost triggers.",
        cpuBoostLevel = "CUSTOM",
        targetFps = 90,
        recommendedFor = "Power users and custom overclocking feel"
    )
}

enum class OverlayLayoutStyle(val displayName: String, val description: String) {
    COMPACT_PILL("Compact Pill", "Minimal pill badge displaying FPS and Temp"),
    CYBER_GAUGE("Cyber Gauge", "Circular esports HUD gauge with active neon arcs"),
    EXPANDED_HUD("Expanded HUD", "Full telemetry banner showing FPS, CPU, RAM, Temp & Ping"),
    MINIMAL_BADGE("Minimal FPS", "Ultra-small FPS counter on screen edge")
}

data class OverlayConfig(
    val isEnabled: Boolean = false,
    val layoutStyle: OverlayLayoutStyle = OverlayLayoutStyle.EXPANDED_HUD,
    val opacity: Float = 0.90f,
    val accentColorHex: Long = 0xFF00FF9D, // CyberLime
    val showFps: Boolean = true,
    val showCpu: Boolean = true,
    val showRam: Boolean = true,
    val showTemp: Boolean = true,
    val showPing: Boolean = true,
    val tempAlertThresholdC: Float = 42.0f,
    val isVibrationAlertEnabled: Boolean = true,
    val isAudioAlertEnabled: Boolean = true,
    val posX: Int = 100,
    val posY: Int = 150
)

data class AutoBoostConfig(
    val isAutoBoostEnabled: Boolean = true,
    val boostOnGameLaunch: Boolean = true,
    val ramThresholdPercent: Int = 80,
    val cpuThresholdPercent: Int = 85,
    val autoCleanIntervalMinutes: Int = 15
)

data class AppProcessItem(
    val packageName: String,
    val appName: String,
    val memoryUsageMb: Long,
    val isGame: Boolean = false,
    val isProtected: Boolean = false,
    val isSelectedForKill: Boolean = true
)

data class BoostResult(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val ramFreedMb: Long,
    val killedProcessesCount: Int,
    val cpuLoadBefore: Int,
    val cpuLoadAfter: Int,
    val message: String
)

data class GameItem(
    val packageName: String,
    val title: String,
    val isInstalledOnDevice: Boolean = true,
    val lastBoostTimestamp: Long = 0L,
    val targetFps: Int = 60,
    val preferredProfile: GamingProfile = GamingProfile.ULTRA
)
