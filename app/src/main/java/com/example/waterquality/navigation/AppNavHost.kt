package com.example.waterquality.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.waterquality.ui.screen.auth.AuthViewModel
import com.example.waterquality.ui.screen.auth.LoginScreen
import com.example.waterquality.ui.screen.auth.RegisterScreen
import com.example.waterquality.ui.screen.homepage.HomeScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel// Pass ViewModel untuk cek status login awal
) {
    val startDestination = if (authViewModel.isUserLoggedIn()) Routes.HOME else Routes.LOGIN

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
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) // Hapus semua history backstack
                    }
                }
//                onNavigateToMaps = { navController.navigate(Routes.Maps) },
//                onLogout = {
//                    authViewModel.logout()
//                    navController.navigate(Routes.Login) {
//                        popUpTo(0) // Hapus semua history backstack
//                    }
//                }
            )
        }
        // kalau ada detail page, taruh di sini
        // composable(Routes.DETAIL) { DetailScreen() }
    }
}
