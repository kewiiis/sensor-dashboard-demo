package com.isep.sensordashboard.model

data class SensorReading(
    val type: SensorType,
    val values: List<Float>,
    val timestampNanos: Long
)
