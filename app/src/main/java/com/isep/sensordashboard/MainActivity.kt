package com.isep.sensordashboard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.isep.sensordashboard.ui.theme.MyApplicationTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Column(Modifier.padding(padding).padding(24.dp)) {
                        Text("Sensors demo", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(16.dp))
                        SensorReader(
                            title = "Accelerometer (m/s²)",
                            sensorType = Sensor.TYPE_ACCELEROMETER,
                            labels = listOf("x", "y", "z")
                        )
                        Spacer(Modifier.height(16.dp))
                        SensorReader(
                            title = "Gyroscope (rad/s)",
                            sensorType = Sensor.TYPE_GYROSCOPE,
                            labels = listOf("x", "y", "z")
                        )
                        Spacer(Modifier.height(16.dp))
                        SensorReader(
                            title = "Light (lx)",
                            sensorType = Sensor.TYPE_LIGHT,
                            labels = listOf("lux")
                        )
                        Spacer(Modifier.height(16.dp))
                        AvailableSensorsList()
                    }
                }
            }
        }
    }
}

@Composable
fun SensorReader(
    title: String,
    sensorType: Int,
    labels: List<String>
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val sensor = remember { sensorManager.getDefaultSensor(sensorType) }

    // On garde toujours un tableau de 3 valeurs, on affichera seulement ce qui est nécessaire
    var values by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    DisposableEffect(sensor) {
        if (sensor == null) return@DisposableEffect onDispose { }
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Copie défensive pour Compose
                val v = event.values
                values = floatArrayOf(
                    v.getOrNull(0) ?: 0f,
                    v.getOrNull(1) ?: 0f,
                    v.getOrNull(2) ?: 0f
                )
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    Text(title, style = MaterialTheme.typography.titleMedium)
    if (sensor == null) {
        Text("• Not available on this device")
    } else {
        labels.forEachIndexed { i, label ->
            Text("$label = ${values.getOrNull(i) ?: 0f}")
        }
    }
}

@Composable
fun AvailableSensorsList() {
    val context = LocalContext.current
    val manager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val sensors = remember { manager.getSensorList(Sensor.TYPE_ALL).map { it.name } }
    Text("Available sensors:", style = MaterialTheme.typography.titleMedium)
    LazyColumn {
        items(sensors) { name -> Text("• $name") }
    }
}
