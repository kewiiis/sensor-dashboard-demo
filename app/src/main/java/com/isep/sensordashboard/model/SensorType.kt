package com.isep.sensordashboard.model

enum class SensorType(val androidType: Int, val dims: Int, val displayName: String) {
    ACCELEROMETER(android.hardware.Sensor.TYPE_ACCELEROMETER, 3, "Accelerometer"),
    GYROSCOPE(android.hardware.Sensor.TYPE_GYROSCOPE, 3, "Gyroscope"),
    LIGHT(android.hardware.Sensor.TYPE_LIGHT, 1, "Light");
}
