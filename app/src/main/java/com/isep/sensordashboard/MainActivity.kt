package com.isep.sensordashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isep.sensordashboard.nav.AppNav
import com.isep.sensordashboard.ui.theme.SensorDashboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SensorDashboardTheme {
                val sensorsViewModel: SensorsViewModel = viewModel(factory = SensorsViewModel.Factory)
                AppNav(viewModel = sensorsViewModel)
            }
        }
    }
}
