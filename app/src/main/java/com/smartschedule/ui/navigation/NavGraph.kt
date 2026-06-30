package com.smartschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartschedule.ui.home.HomeScreen
import com.smartschedule.ui.home.HomeViewModel

object Routes {
    const val HOME = "home"
}

@Composable
fun SmartScheduleNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    onRequestNotificationPermission: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onRequestNotificationPermission = onRequestNotificationPermission
            )
        }
    }
}
