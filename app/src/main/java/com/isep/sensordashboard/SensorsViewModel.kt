package com.isep.sensordashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.isep.sensordashboard.data.SensorRepository
import com.isep.sensordashboard.data.SensorRepositoryImpl
import com.isep.sensordashboard.model.SensorReading
import com.isep.sensordashboard.model.SensorType
import com.isep.sensordashboard.sensors.AndroidSensorDataSource
import com.isep.sensordashboard.sensors.SamplingRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val available: List<SensorType> = emptyList(),
    val currentType: SensorType? = null,
    val rate: SamplingRate = SamplingRate.GAME,
    val lastReading: SensorReading? = null,
    val error: String? = null
)

class SensorsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: SensorRepository = SensorRepositoryImpl(AndroidSensorDataSource(app))

    private val _state = MutableStateFlow(
        UiState(available = repo.availableSensors())
    )
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var readingsJob: kotlinx.coroutines.Job? = null

    fun selectSensor(type: SensorType) {
        _state.update { it.copy(currentType = type, error = null, lastReading = null) }
        restartStream()
    }

    fun setRate(rate: SamplingRate) {
        _state.update { it.copy(rate = rate) }
        restartStream()
    }

    private fun restartStream() {
        readingsJob?.cancel()
        val type = _state.value.currentType ?: return
        val rate = _state.value.rate
        readingsJob = viewModelScope.launch {
            repo.readings(type, rate)
                .catch { e -> _state.update { it.copy(error = e.message) } }
                .collect { reading -> _state.update { it.copy(lastReading = reading) } }
        }
    }
}
