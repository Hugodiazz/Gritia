package com.devdiaz.gritia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devdiaz.gritia.ui.login.LoginScreen
import com.devdiaz.gritia.ui.main.MainScreen
import com.devdiaz.gritia.ui.workout.WorkoutSummaryScreen

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
        composable("main") {
            MainScreen(
                    onNavigateToAddMetric = { navController.navigate("add_metric") },
                    onNavigateToWorkout = { routineId ->
                        navController.navigate("workout_log/$routineId")
                    },
                    onNavigateToHistory = { navController.navigate("measurement_history") },
                    onNavigateToCreateRoutine = { navController.navigate("create_routine") },
                    onNavigateToLogin = {
                        navController.navigate("login") { popUpTo("main") { inclusive = true } }
                    }
            )
        }
        composable("add_metric") {
            com.devdiaz.gritia.ui.add.AddScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("create_routine") { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val selectedExercises =
                    savedStateHandle.get<List<com.devdiaz.gritia.model.Exercise>>(
                            "selected_exercises"
                    )

            // Clear the result once retrieved to prevent re-adding on configuration changes if
            // needed,
            // though recomposition with same value might be handled in screen.
            // Better to let the screen consume it via LaunchedEffect key.
            if (selectedExercises != null) {
                savedStateHandle.remove<List<com.devdiaz.gritia.model.Exercise>>(
                        "selected_exercises"
                )
            }

            com.devdiaz.gritia.ui.create.CreateRoutineScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToExerciseSelector = { navController.navigate("exercise_selector") },
                    newExercises = selectedExercises
            )
        }
        composable("exercise_selector") {
            com.devdiaz.gritia.ui.create.ExerciseSelectorScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onExercisesSelected = { exercises ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selected_exercises",
                                ArrayList(exercises)
                        )
                        navController.popBackStack()
                    }
            )
        }
        composable(
                "workout_log/{routineId}",
                arguments =
                        listOf(
                                androidx.navigation.navArgument("routineId") {
                                    type = androidx.navigation.NavType.LongType
                                }
                        )
        ) {
            com.devdiaz.gritia.ui.workout.WorkoutLogScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSummary = { routineName, duration, volume ->
                        val encodedName = java.net.URLEncoder.encode(routineName, "UTF-8")
                        navController.navigate("workout_summary/$encodedName/$duration/$volume") {
                            popUpTo("workout_log/{routineId}") { inclusive = true }
                        }
                    }
            )
        }
        composable(
                "workout_summary/{routineName}/{duration}/{volume}",
                arguments =
                        listOf(
                                navArgument("routineName") { type = NavType.StringType },
                                navArgument("duration") { type = NavType.LongType },
                                navArgument("volume") { type = NavType.FloatType }
                        )
        ) { backStackEntry ->
            val routineName = backStackEntry.arguments?.getString("routineName") ?: ""
            val durationSeconds = backStackEntry.arguments?.getLong("duration") ?: 0L
            val volume = backStackEntry.arguments?.getFloat("volume") ?: 0f

            // Format duration here or pass helper
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            val durationStr =
                    String.format("%02d:%02d:%02d", durationSeconds / 3600, minutes % 60, seconds)

            // Format volume
            val volumeStr = String.format("%.0f kg", volume)

            WorkoutSummaryScreen(
                    routineName = java.net.URLDecoder.decode(routineName, "UTF-8"),
                    duration = durationStr,
                    totalVolume = volumeStr,
                    onSaveClick = {
                        navController.navigate("main") { popUpTo("main") { inclusive = true } }
                    }
            )
        }
        composable("measurement_history") {
            com.devdiaz.gritia.ui.metrics.MeasurementHistoryScreen(
                    onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
