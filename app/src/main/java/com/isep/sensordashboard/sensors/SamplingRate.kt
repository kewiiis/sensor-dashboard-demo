package com.isep.sensordashboard.sensors

enum class SamplingRate(
    val samplingPeriodMicros: Int,
    val label: String,
    val description: String,
    val requiresHighSamplingPermission: Boolean = false
) {
    UI(
        samplingPeriodMicros = 16_666,
        label = "UI",
        description = "Approx. 60 Hz"
    ),
    NORMAL(
        samplingPeriodMicros = 200_000,
        label = "Normal",
        description = "Default system rate (~5 Hz)"
    ),
    GAME(
        samplingPeriodMicros = 20_000,
        label = "Game",
        description = "Around 50 Hz"
    ),
    FASTEST(
        samplingPeriodMicros = 5_000,
        label = "Fastest",
        description = "Highest available (0 ms on gyro/accelerometer)",
        requiresHighSamplingPermission = true
    );
}
