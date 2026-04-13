package com.example.waterquality.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.waterquality.ui.screen.auth.AuthViewModel
import com.example.waterquality.ui.screen.auth.LoginScreen
import com.example.waterquality.ui.screen.auth.RegisterScreen
import com.example.waterquality.ui.screen.homepage.HomeScreen
import com.example.waterquality.ui.screen.homepage.SensorViewModel
import com.example.waterquality.ui.screen.maps.OsmMapView

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel ()// Pass ViewModel untuk cek status login awal
) {
    val startDestination = if (authViewModel.isUserLoggedIn()) Routes.HOME else Routes.LOGIN
    val currentUser = authViewModel.currentUser
    val username = currentUser?.displayName ?: "No Name"
    val email = currentUser?.email ?: "No Email"// State untuk IP Settings (yang lama)
    val sharedSensorViewModel: SensorViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController, authViewModel)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = sharedSensorViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) // Hapus semua history backstack
                    }
                },
                username = username,
                email = email,
                onNavigateToMaps = { navController.navigate(Routes.MAPS) }
            )
        }
        composable(Routes.MAPS) {
            val uiState by sharedSensorViewModel.sensorData.collectAsState()
            OsmMapView(points = uiState.locationHistory)
        }
        // kalau ada detail page, taruh di sini
        // composable(Routes.DETAIL) { DetailScreen() }
    }
}
