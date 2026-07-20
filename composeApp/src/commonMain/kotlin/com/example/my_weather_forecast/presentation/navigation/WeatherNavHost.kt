package com.example.my_weather_forecast.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.my_weather_forecast.presentation.overview.OverviewScreen

@Composable
fun WeatherNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.OVERVIEW) {
        composable(Routes.OVERVIEW) {
            OverviewScreen(
                onOpenSearch = { navController.navigate(Routes.SEARCH) },
                onOpenDetail = { locationId -> navController.navigate(Routes.detail(locationId)) },
            )
        }
    }
}
