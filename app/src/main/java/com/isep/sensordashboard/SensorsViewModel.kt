package com.isep.sensordashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.isep.sensordashboard.data.SensorRepository
import com.isep.sensordashboard.data.SensorRepositoryImpl
import com.isep.sensordashboard.model.SensorReading
import com.isep.sensordashboard.model.SensorType
import com.isep.sensordashboard.sensors.AndroidSensorDataSource
import com.isep.sensordashboard.sensors.SamplingRate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val available: List<SensorType> = emptyList(),
    val currentType: SensorType? = null,
    val rate: SamplingRate = SamplingRate.NORMAL,
    val lastReading: SensorReading? = null,
    val isStreaming: Boolean = false,
    val error: String? = null
)

class SensorsViewModel(app: Application) : AndroidViewModel(app) {

    private val appContext = app
    private val repository: SensorRepository = SensorRepositoryImpl(AndroidSensorDataSource(app))
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var readingsJob: Job? = null

    init {
        refreshAvailableSensors()
    }

    fun refreshAvailableSensors() {
        val sensors = repository.availableSensors()
        val current = _state.value.currentType
        val selected = current?.takeIf { it in sensors } ?: sensors.firstOrNull()
        val message = if (selected == null) appContext.getString(R.string.dashboard_empty) else null

        _state.update {
            it.copy(
                available = sensors,
                currentType = selected,
                error = message,
                lastReading = null,
                isStreaming = false
            )
        }

        if (selected != null) {
            restartStream()
        } else {
            readingsJob?.cancel()
        }
    }

    fun selectSensor(type: SensorType) {
        if (_state.value.currentType == type) return
        _state.update { it.copy(currentType = type, lastReading = null, error = null) }
        restartStream()
    }

    fun setRate(rate: SamplingRate) {
        if (_state.value.rate == rate) return
        _state.update { it.copy(rate = rate, lastReading = null, error = null) }
        restartStream()
    }

    private fun restartStream() {
        readingsJob?.cancel()
        val sensorType = _state.value.currentType ?: return
        val rate = _state.value.rate

        _state.update { it.copy(isStreaming = true, error = null, lastReading = null) }

        readingsJob = viewModelScope.launch {
            repository.readings(sensorType, rate)
                .catch { throwable ->
                    _state.update {
                        it.copy(
                            error = throwable.message ?: "Failed to read ${sensorType.displayName}.",
                            isStreaming = false,
                            lastReading = null
                        )
                    }
                }
                .collect { reading ->
                    _state.update {
                        it.copy(
                            lastReading = reading,
                            error = null,
                            isStreaming = true
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        readingsJob?.cancel()
        super.onCleared()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                SensorsViewModel(application)
            }
        }
    }
}
