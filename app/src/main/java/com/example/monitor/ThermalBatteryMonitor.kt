package com.example.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import com.example.model.ThermalLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ThermalBatteryState(
    val temperatureC: Float = 36.5f,
    val temperatureF: Float = 97.7f,
    val thermalLevel: ThermalLevel = ThermalLevel.NORMAL,
    val batteryPercent: Int = 85,
    val isCharging: Boolean = false,
    val isHighTempAlert: Boolean = false,
    val rawHardwareThermalStatus: Int = 0
)

class ThermalBatteryMonitor(private val context: Context) {

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    private val _state = MutableStateFlow(ThermalBatteryState())
    val state: StateFlow<ThermalBatteryState> = _state.asStateFlow()

    private var thresholdC: Float = 42.0f
    private var simulatedTempOffsetC: Float = 0f
    private var isSimulatingAlert: Boolean = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 350)
                val baseTempC = tempTenths / 10.0f
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 80)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                val pct = if (scale > 0) ((level * 100) / scale) else level
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                updateTemperature(baseTempC, pct, charging)
            }
        }
    }

    private var thermalListener: PowerManager.OnThermalStatusChangedListener? = null

    init {
        registerBatteryReceiver()
        registerThermalListener()
    }

    fun setThreshold(threshold: Float) {
        thresholdC = threshold
        evaluateAlert()
    }

    fun setSimulatedTempOffset(offsetC: Float) {
        simulatedTempOffsetC = offsetC
        val current = _state.value
        updateTemperature(current.temperatureC - simulatedTempOffsetC + offsetC, current.batteryPercent, current.isCharging)
    }

    fun triggerTestAlert(active: Boolean) {
        isSimulatingAlert = active
        evaluateAlert()
    }

    private fun registerBatteryReceiver() {
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val sticky = context.registerReceiver(batteryReceiver, filter)
            if (sticky != null) {
                batteryReceiver.onReceive(context, sticky)
            }
        } catch (_: Exception) {
            updateTemperature(36.0f, 85, false)
        }
    }

    private fun registerThermalListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null) {
            try {
                thermalListener = PowerManager.OnThermalStatusChangedListener { status ->
                    val thermalLevel = when (status) {
                        PowerManager.THERMAL_STATUS_NONE -> ThermalLevel.NORMAL
                        PowerManager.THERMAL_STATUS_LIGHT -> ThermalLevel.WARM
                        PowerManager.THERMAL_STATUS_MODERATE -> ThermalLevel.WARM
                        PowerManager.THERMAL_STATUS_SEVERE -> ThermalLevel.HOT
                        PowerManager.THERMAL_STATUS_CRITICAL -> ThermalLevel.CRITICAL
                        PowerManager.THERMAL_STATUS_EMERGENCY -> ThermalLevel.EMERGENCY
                        else -> ThermalLevel.NORMAL
                    }
                    _state.value = _state.value.copy(
                        thermalLevel = thermalLevel,
                        rawHardwareThermalStatus = status
                    )
                    evaluateAlert()
                }
                powerManager.addThermalStatusListener(context.mainExecutor, thermalListener!!)
            } catch (_: Exception) {}
        }
    }

    private fun updateTemperature(baseC: Float, batteryPct: Int, charging: Boolean) {
        val effectiveC = if (isSimulatingAlert) {
            thresholdC + 3.5f
        } else {
            (baseC + simulatedTempOffsetC).coerceIn(20f, 65f)
        }
        val tempF = (effectiveC * 9f / 5f) + 32f

        val calculatedLevel = when {
            effectiveC >= 48f -> ThermalLevel.CRITICAL
            effectiveC >= 44f -> ThermalLevel.HOT
            effectiveC >= 39f -> ThermalLevel.WARM
            effectiveC >= 32f -> ThermalLevel.NORMAL
            else -> ThermalLevel.COOL
        }

        _state.value = _state.value.copy(
            temperatureC = effectiveC,
            temperatureF = tempF,
            thermalLevel = calculatedLevel,
            batteryPercent = batteryPct,
            isCharging = charging
        )
        evaluateAlert()
    }

    private fun evaluateAlert() {
        val currentC = _state.value.temperatureC
        val isTriggered = isSimulatingAlert || currentC >= thresholdC ||
                _state.value.thermalLevel == ThermalLevel.HOT ||
                _state.value.thermalLevel == ThermalLevel.CRITICAL ||
                _state.value.thermalLevel == ThermalLevel.EMERGENCY

        _state.value = _state.value.copy(isHighTempAlert = isTriggered)
    }

    fun unregister() {
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (_: Exception) {}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && thermalListener != null && powerManager != null) {
            try {
                powerManager.removeThermalStatusListener(thermalListener!!)
            } catch (_: Exception) {}
        }
    }
}
