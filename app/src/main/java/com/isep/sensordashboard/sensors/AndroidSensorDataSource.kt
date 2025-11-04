package com.isep.sensordashboard.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.isep.sensordashboard.model.SensorReading
import com.isep.sensordashboard.model.SensorType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidSensorDataSource(
    context: Context
) {
    private val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun available(): List<SensorType> =
        SensorType.entries.filter { manager.getDefaultSensor(it.androidType) != null }

    fun stream(type: SensorType, rate: SamplingRate): Flow<SensorReading> = callbackFlow {
        val sensor: Sensor? = manager.getDefaultSensor(type.androidType)
        if (sensor == null) {
            close(IllegalStateException("${type.displayName} not available on this device"))
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val values = List(type.dimensions) { index ->
                    event.values.getOrNull(index) ?: 0f
                }
                trySend(
                    SensorReading(
                        type = type,
                        values = values,
                        timestampNanos = event.timestamp
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        val samplingPeriodUs = if (rate.requiresHighSamplingPermission && type.supportsHighSampling) {
            0
        } else {
            rate.samplingPeriodMicros
        }

        manager.registerListener(listener, sensor, samplingPeriodUs, 0)
        awaitClose { manager.unregisterListener(listener) }
    }
}
