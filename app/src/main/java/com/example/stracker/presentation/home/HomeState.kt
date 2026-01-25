package com.example.stracker.presentation.home

import com.example.stracker.domain.model.Workout

data class HomeState(
    val lastWorkout: Workout? = null,
    val activeWorkout: Workout? = null,
    val totalWorkouts: Int = 0,
    val totalExercises: Int = 0,
    val activeDays: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface HomeEvent {
    data object StartWorkout : HomeEvent
    data object ContinueWorkout : HomeEvent
    data object Refresh : HomeEvent
}
