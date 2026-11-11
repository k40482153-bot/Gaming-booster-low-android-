package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.GamingProfile
import com.example.model.PerformanceStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Apex Booster", appName)
  }

  @Test
  fun `verify gaming profile targets`() {
    assertEquals(120, GamingProfile.ULTRA.targetFps)
    assertEquals(60, GamingProfile.BALANCED.targetFps)
  }

  @Test
  fun `verify high temperature threshold alert logic`() {
    val normalStats = PerformanceStats(temperatureC = 37.0f, highTempThresholdC = 42.0f, isHighTempAlert = false)
    val overheatingStats = PerformanceStats(temperatureC = 43.5f, highTempThresholdC = 42.0f, isHighTempAlert = true)
    assertTrue(overheatingStats.isHighTempAlert)
    assertTrue(overheatingStats.temperatureC > normalStats.temperatureC)
  }
}
