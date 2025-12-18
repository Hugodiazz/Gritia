package com.devdiaz.gritia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devdiaz.gritia.ui.login.LoginScreen
import com.devdiaz.gritia.ui.main.MainScreen

@Composable
fun GritiaNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("main") { popUpTo("login") { inclusive = true } }
                    }
            )
        }
        composable("main") { MainScreen() }
    }
}
