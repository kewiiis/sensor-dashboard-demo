package com.isep.sensordashboard.model

import android.hardware.Sensor

enum class SensorType(
    val androidType: Int,
    val axisLabels: List<String>,
    val unitSuffix: String,
    val displayName: String,
    val supportsHighSampling: Boolean
) {
    ACCELEROMETER(
        androidType = Sensor.TYPE_ACCELEROMETER,
        axisLabels = listOf("x", "y", "z"),
        unitSuffix = "m/s²",
        displayName = "Accelerometer",
        supportsHighSampling = true
    ),
    GYROSCOPE(
        androidType = Sensor.TYPE_GYROSCOPE,
        axisLabels = listOf("x", "y", "z"),
        unitSuffix = "rad/s",
        displayName = "Gyroscope",
        supportsHighSampling = true
    ),
    LIGHT(
        androidType = Sensor.TYPE_LIGHT,
        axisLabels = listOf("lux"),
        unitSuffix = "lx",
        displayName = "Light",
        supportsHighSampling = false
    );

    val dimensions: Int = axisLabels.size
}
