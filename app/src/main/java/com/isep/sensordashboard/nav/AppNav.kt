package com.isep.sensordashboard.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.isep.sensordashboard.SensorsViewModel
import com.isep.sensordashboard.ui.dashboard.DashboardScreen
import com.isep.sensordashboard.ui.detail.DetailScreen

private object Destinations {
    const val Dashboard = "dashboard"
    const val Detail = "detail"
}

@Composable
fun AppNav(viewModel: SensorsViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Destinations.Dashboard
    ) {
        composable(Destinations.Dashboard) {
            DashboardScreen(
                state = uiState,
                onSensorSelected = { sensor -> viewModel.selectSensor(sensor) },
                onNavigateToDetail = {
                    navController.navigate(Destinations.Detail) {
                        launchSingleTop = true
                    }
                },
                onRefresh = { viewModel.refreshAvailableSensors() }
            )
        }
        composable(Destinations.Detail) {
            DetailScreen(
                state = uiState,
                onBack = { navController.popBackStack() },
                onSelectSensor = { sensor -> viewModel.selectSensor(sensor) },
                onSelectRate = { rate -> viewModel.setRate(rate) }
            )
        }
    }
}
