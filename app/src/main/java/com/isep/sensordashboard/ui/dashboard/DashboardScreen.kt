package com.isep.sensordashboard.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.isep.sensordashboard.R
import com.isep.sensordashboard.UiState
import com.isep.sensordashboard.model.SensorReading
import com.isep.sensordashboard.model.SensorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: UiState,
    onSensorSelected: (SensorType) -> Unit,
    onNavigateToDetail: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_dashboard)) },
                actions = {
                    TextButton(onClick = onRefresh) {
                        Text(stringResource(R.string.action_refresh))
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
            if (state.available.isEmpty()) {
                EmptySensorsState(
                    error = state.error,
                    onRefresh = onRefresh
                )
            } else {
                val readingByType = remember(state.lastReading) {
                    state.lastReading?.let { mapOf(it.type to it) } ?: emptyMap()
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.available) { type ->
                        val isSelected = state.currentType == type
                        val reading = readingByType[type]
                        SensorSummaryCard(
                            sensorType = type,
                            reading = reading,
                            isSelected = isSelected,
                            onClick = {
                                onSensorSelected(type)
                                onNavigateToDetail()
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SensorSummaryCard(
    sensorType: SensorType,
    reading: SensorReading?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColors = if (isSelected) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        CardDefaults.cardColors()
    }
    Card(
        onClick = onClick,
        colors = containerColors
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = sensorType.displayName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (reading != null) {
                sensorType.axisLabels.zip(reading.values).forEach { (label, value) ->
                    val suffix = sensorType.unitSuffix.takeIf { it.isNotEmpty() }?.let { " $it" } ?: ""
                    Text("$label = ${formatReading(value)}$suffix")
                }
            } else {
                Text(
                    text = stringResource(R.string.dashboard_card_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptySensorsState(
    error: String?,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = error ?: stringResource(R.string.dashboard_empty),
            style = MaterialTheme.typography.bodyLarge
        )
        TextButton(onClick = onRefresh) {
            Text(stringResource(R.string.dashboard_empty_retry))
        }
    }
}

private fun formatReading(value: Float): String = "%.2f".format(value)
