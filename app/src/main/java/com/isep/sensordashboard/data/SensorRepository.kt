package com.isep.sensordashboard.data

import com.isep.sensordashboard.model.SensorReading
import com.isep.sensordashboard.model.SensorType
import com.isep.sensordashboard.sensors.SamplingRate
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    fun availableSensors(): List<SensorType>
    fun readings(type: SensorType, rate: SamplingRate): Flow<SensorReading>
}
