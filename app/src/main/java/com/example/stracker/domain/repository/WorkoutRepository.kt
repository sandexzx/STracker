package com.example.stracker.domain.repository

import com.example.stracker.domain.model.ExerciseSet
import com.example.stracker.domain.model.Workout
import com.example.stracker.domain.model.WorkoutExercise
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkouts(): Flow<List<Workout>>
    fun getCompletedWorkouts(): Flow<List<Workout>>
    fun getActiveWorkoutFlow(): Flow<Workout?>
    fun getWorkoutByIdFlow(id: Long): Flow<Workout?>
    fun getCompletedWorkoutsCount(): Flow<Int>
    
    suspend fun getActiveWorkout(): Workout?
    suspend fun getWorkoutById(id: Long): Workout?
    suspend fun getLastCompletedWorkout(): Workout?
    
    suspend fun startWorkout(): Long
    suspend fun finishWorkout(workoutId: Long, note: String? = null)
    suspend fun discardWorkout(workoutId: Long)
    
    suspend fun addExerciseToWorkout(workoutId: Long, exerciseId: Long): Long
    suspend fun removeExerciseFromWorkout(workoutExerciseId: Long)
    
    suspend fun addSet(workoutExerciseId: Long, weight: Float, reps: Int, rpe: Int? = null, isWarmup: Boolean = false): Long
    suspend fun updateSet(set: ExerciseSet, workoutExerciseId: Long)
    suspend fun completeSet(setId: Long)
    suspend fun deleteSet(setId: Long)
    
    suspend fun getLastPerformance(exerciseId: Long, limit: Int = 5): List<WorkoutExercise>
    suspend fun getExercisesForWorkout(workoutId: Long): List<WorkoutExercise>
}
