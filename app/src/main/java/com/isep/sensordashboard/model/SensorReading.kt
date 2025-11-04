package com.isep.sensordashboard.model

data class SensorReading(
    val type: SensorType,
    val values: FloatArray,
    val timestampNanos: Long
)
