package com.example.monitor

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Choreographer
import android.view.Display
import android.view.WindowManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.CopyOnWriteArrayList

class FpsMonitor(private val context: Context) : Choreographer.FrameCallback {

    private val _fps = MutableStateFlow(60)
    val fps: StateFlow<Int> = _fps.asStateFlow()

    private val _onePercentLow = MutableStateFlow(55)
    val onePercentLow: StateFlow<Int> = _onePercentLow.asStateFlow()

    private val _frameTimeMs = MutableStateFlow(16.6f)
    val frameTimeMs: StateFlow<Float> = _frameTimeMs.asStateFlow()

    private val _maxRefreshRate = MutableStateFlow(60)
    val maxRefreshRate: StateFlow<Int> = _maxRefreshRate.asStateFlow()

    private var frameCount = 0
    private var lastIntervalTimeNanos: Long = 0L
    private var lastFrameTimeNanos: Long = 0L
    private var isRunning = false

    private val frameDurationsMs = CopyOnWriteArrayList<Float>()

    init {
        detectMaxRefreshRate()
    }

    private fun detectMaxRefreshRate() {
        try {
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
            val display = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
                ?: (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay
            val refreshRate = display?.refreshRate?.toInt() ?: 60
            _maxRefreshRate.value = if (refreshRate in 30..240) refreshRate else 60
            _fps.value = _maxRefreshRate.value
        } catch (_: Exception) {
            _maxRefreshRate.value = 60
        }
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            frameCount = 0
            lastIntervalTimeNanos = System.nanoTime()
            lastFrameTimeNanos = lastIntervalTimeNanos
            frameDurationsMs.clear()
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun stop() {
        isRunning = false
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isRunning) return

        if (lastFrameTimeNanos > 0) {
            val durationMs = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000f
            if (durationMs > 0 && durationMs < 500f) {
                frameDurationsMs.add(durationMs)
                if (frameDurationsMs.size > 120) {
                    frameDurationsMs.removeAt(0)
                }
                _frameTimeMs.value = String.format("%.1f", durationMs).toFloatOrNull() ?: 16.6f
            }
        }
        lastFrameTimeNanos = frameTimeNanos
        frameCount++

        val elapsedNanos = frameTimeNanos - lastIntervalTimeNanos
        if (elapsedNanos >= 1_000_000_000L) { // 1 second window
            val measuredFps = ((frameCount * 1_000_000_000L) / elapsedNanos).toInt()
            val clampedFps = measuredFps.coerceIn(15, _maxRefreshRate.value.coerceAtLeast(60))
            _fps.value = clampedFps

            // Calculate 1% Low FPS
            if (frameDurationsMs.isNotEmpty()) {
                val sorted = frameDurationsMs.sortedDescending()
                val onePercentIndex = (sorted.size * 0.01).toInt().coerceAtMost(sorted.size - 1)
                val worstFrameTimeMs = sorted[onePercentIndex]
                val oneLow = if (worstFrameTimeMs > 0) (1000f / worstFrameTimeMs).toInt() else clampedFps
                _onePercentLow.value = oneLow.coerceIn(10, clampedFps)
            } else {
                _onePercentLow.value = (clampedFps * 0.9f).toInt()
            }

            frameCount = 0
            lastIntervalTimeNanos = frameTimeNanos
        }

        Choreographer.getInstance().postFrameCallback(this)
    }
}
