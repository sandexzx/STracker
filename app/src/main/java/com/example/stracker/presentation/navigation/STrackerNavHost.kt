package com.example.stracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.stracker.presentation.exercise.create.CreateExerciseScreen
import com.example.stracker.presentation.exercise.detail.ExerciseDetailScreen
import com.example.stracker.presentation.exercise.library.ExerciseLibraryScreen
import com.example.stracker.presentation.exercise.picker.ExercisePickerScreen
import com.example.stracker.presentation.home.HomeScreen
import com.example.stracker.presentation.settings.SettingsScreen
import com.example.stracker.presentation.workout.active.ActiveWorkoutScreen
import com.example.stracker.presentation.workout.detail.WorkoutDetailScreen
import com.example.stracker.presentation.workout.history.WorkoutHistoryScreen

@Composable
fun STrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartWorkout = { workoutId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(workoutId))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.WorkoutHistory.route)
                },
                onNavigateToExercises = {
                    navController.navigate(Screen.ExerciseLibrary.route)
                },
                onContinueWorkout = { workoutId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(workoutId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(
            route = Screen.ActiveWorkout.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            ActiveWorkoutScreen(
                workoutId = workoutId,
                onNavigateBack = { navController.popBackStack() },
                onAddExercise = {
                    navController.navigate(Screen.ExercisePicker.createRoute(workoutId))
                },
                onWorkoutFinished = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
        
        composable(Screen.WorkoutHistory.route) {
            WorkoutHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(workoutId))
                },
                onNavigateToExercises = {
                    navController.navigate(Screen.ExerciseLibrary.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        
        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            WorkoutDetailScreen(
                workoutId = workoutId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ExerciseLibrary.route) {
            ExerciseLibraryScreen(
                onNavigateBack = { navController.popBackStack() },
                onExerciseClick = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                },
                onCreateExercise = {
                    navController.navigate(Screen.CreateExercise.createRoute())
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.WorkoutHistory.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        
        composable(
            route = Screen.CreateExercise.route,
            arguments = listOf(
                navArgument("exerciseId") { 
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            CreateExerciseScreen(
                onNavigateBack = { navController.popBackStack() },
                onExerciseCreated = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ExerciseDetail.route,
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: return@composable
            ExerciseDetailScreen(
                exerciseId = exerciseId,
                onNavigateBack = { navController.popBackStack() },
                onEditExercise = { id ->
                    navController.navigate(Screen.CreateExercise.createRoute(id))
                },
                onExerciseDeleted = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.ExercisePicker.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            ExercisePickerScreen(
                workoutId = workoutId,
                onNavigateBack = { navController.popBackStack() },
                onExerciseSelected = { 
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
