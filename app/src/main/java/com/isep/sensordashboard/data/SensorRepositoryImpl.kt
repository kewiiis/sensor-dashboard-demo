package com.isep.sensordashboard.data

import com.isep.sensordashboard.model.SensorReading
import com.isep.sensordashboard.model.SensorType
import com.isep.sensordashboard.sensors.AndroidSensorDataSource
import com.isep.sensordashboard.sensors.SamplingRate
import kotlinx.coroutines.flow.Flow

class SensorRepositoryImpl(
    private val ds: AndroidSensorDataSource
) : SensorRepository {
    override fun availableSensors(): List<SensorType> = ds.available()
    override fun readings(type: SensorType, rate: SamplingRate): Flow<SensorReading> =
        ds.stream(type, rate)
}
