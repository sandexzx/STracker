package com.example.stracker.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ActiveWorkout : Screen("workout/active/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout/active/$workoutId"
    }
    data object WorkoutHistory : Screen("workout/history")
    data object WorkoutDetail : Screen("workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout/$workoutId"
    }
    data object ExerciseLibrary : Screen("exercises")
    data object ExerciseDetail : Screen("exercises/{exerciseId}") {
        fun createRoute(exerciseId: Long) = "exercises/$exerciseId"
    }
    data object CreateExercise : Screen("exercises/create?exerciseId={exerciseId}") {
        fun createRoute(exerciseId: Long? = null) = if (exerciseId != null) "exercises/create?exerciseId=$exerciseId" else "exercises/create"
    }
    data object ExercisePicker : Screen("exercises/picker/{workoutId}") {
        fun createRoute(workoutId: Long) = "exercises/picker/$workoutId"
    }
    data object Settings : Screen("settings")
}
