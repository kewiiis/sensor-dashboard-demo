package com.isep.sensordashboard.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.isep.sensordashboard.R
import com.isep.sensordashboard.UiState
import com.isep.sensordashboard.model.SensorReading
import com.isep.sensordashboard.model.SensorType
import com.isep.sensordashboard.sensors.SamplingRate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: UiState,
    onBack: () -> Unit,
    onSelectSensor: (SensorType) -> Unit,
    onSelectRate: (SamplingRate) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.nav_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val currentSensor = state.currentType

            SensorSelectionRow(
                sensors = state.available,
                current = currentSensor,
                onSelectSensor = onSelectSensor
            )

            if (currentSensor == null) {
                Text(
                    text = stringResource(R.string.detail_select_prompt),
                    style = MaterialTheme.typography.bodyLarge
                )
                return@Column
            }

            SensorReadingsCard(
                currentSensor = currentSensor,
                reading = state.lastReading
            )

            SamplingRateRow(
                selected = state.rate,
                onSelect = onSelectRate
            )

            StreamStatus(
                isStreaming = state.isStreaming,
                error = state.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SensorSelectionRow(
    sensors: List<SensorType>,
    current: SensorType?,
    onSelectSensor: (SensorType) -> Unit
) {
    if (sensors.isEmpty()) return
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(sensors) { sensor ->
            val selected = sensor == current
            FilterChip(
                selected = selected,
                onClick = { onSelectSensor(sensor) },
                label = { Text(sensor.displayName) }
            )
        }
    }
}

@Composable
private fun SensorReadingsCard(
    currentSensor: SensorType,
    reading: SensorReading?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = currentSensor.displayName,
                style = MaterialTheme.typography.titleLarge
            )

            if (reading == null) {
                Text(
                    text = stringResource(R.string.detail_waiting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val values: List<Float> = remember(reading) { reading.values }
                currentSensor.axisLabels.zip(values).forEach { (label, value) ->
                    val suffix = currentSensor.unitSuffix.takeIf { it.isNotEmpty() }?.let { " $it" } ?: ""
                    Text(
                        text = "$label = ${formatReading(value)}$suffix",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SamplingRateRow(
    selected: SamplingRate,
    onSelect: (SamplingRate) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.detail_sampling_rate),
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(SamplingRate.entries.toTypedArray()) { rate ->
                FilterChip(
                    selected = selected == rate,
                    onClick = { onSelect(rate) },
                    label = { Text(rate.label) }
                )
            }
        }
        Text(
            text = selected.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StreamStatus(
    isStreaming: Boolean,
    error: String?
) {
    if (error != null) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        return
    }
    val status = if (isStreaming) {
        stringResource(R.string.status_streaming)
    } else {
        stringResource(R.string.status_paused)
    }
    Text(
        text = status,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Start
    )
}

private fun formatReading(value: Float): String = "%.2f".format(value)
