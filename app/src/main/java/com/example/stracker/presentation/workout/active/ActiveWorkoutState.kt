package com.example.stracker.presentation.workout.active

import com.example.stracker.domain.model.ProgressionAdvice
import com.example.stracker.domain.model.Workout
import com.example.stracker.domain.model.WorkoutExercise

data class ActiveWorkoutState(
    val workout: Workout? = null,
    val elapsedSeconds: Long = 0,
    val lastPerformance: Map<Long, WorkoutExercise> = emptyMap(),
    val progressionAdvice: Map<Long, ProgressionAdvice> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showFinishDialog: Boolean = false,
    val showDiscardDialog: Boolean = false
)

sealed interface ActiveWorkoutEvent {
    data object AddExercise : ActiveWorkoutEvent
    data class RemoveExercise(val workoutExerciseId: Long) : ActiveWorkoutEvent
    data class AddSet(val workoutExerciseId: Long, val weight: Float, val reps: Int, val rpe: Int?, val isWarmup: Boolean = false) : ActiveWorkoutEvent
    data class UpdateSet(val setId: Long, val weight: Float, val reps: Int, val rpe: Int?, val workoutExerciseId: Long) : ActiveWorkoutEvent
    data class DeleteSet(val setId: Long) : ActiveWorkoutEvent
    data object ShowFinishDialog : ActiveWorkoutEvent
    data object HideFinishDialog : ActiveWorkoutEvent
    data object ShowDiscardDialog : ActiveWorkoutEvent
    data object HideDiscardDialog : ActiveWorkoutEvent
    data class FinishWorkout(val note: String?) : ActiveWorkoutEvent
    data object DiscardWorkout : ActiveWorkoutEvent
}
