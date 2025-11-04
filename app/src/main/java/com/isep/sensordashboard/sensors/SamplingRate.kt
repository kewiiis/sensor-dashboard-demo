package com.isep.sensordashboard.sensors

import android.hardware.SensorManager

enum class SamplingRate(val androidDelay: Int) {
    UI(SensorManager.SENSOR_DELAY_UI),
    NORMAL(SensorManager.SENSOR_DELAY_NORMAL),
    GAME(SensorManager.SENSOR_DELAY_GAME),
    FASTEST(SensorManager.SENSOR_DELAY_FASTEST)
}
