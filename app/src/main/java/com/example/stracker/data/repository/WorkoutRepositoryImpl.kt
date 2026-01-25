package com.example.stracker.data.repository

import com.example.stracker.data.local.dao.ExerciseDao
import com.example.stracker.data.local.dao.SetDao
import com.example.stracker.data.local.dao.WorkoutDao
import com.example.stracker.data.local.dao.WorkoutExerciseDao
import com.example.stracker.data.local.entity.SetEntity
import com.example.stracker.data.local.entity.WorkoutEntity
import com.example.stracker.data.local.entity.WorkoutExerciseEntity
import com.example.stracker.data.mapper.toDomain
import com.example.stracker.data.mapper.toEntity
import com.example.stracker.domain.model.ExerciseSet
import com.example.stracker.domain.model.Workout
import com.example.stracker.domain.model.WorkoutExercise
import com.example.stracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val setDao: SetDao,
    private val exerciseDao: ExerciseDao
) : WorkoutRepository {
    
    override fun getAllWorkouts(): Flow<List<Workout>> =
        workoutDao.getAllWorkouts().map { list ->
            list.map { entity -> loadWorkoutWithExercises(entity) }
        }
    
    override fun getCompletedWorkouts(): Flow<List<Workout>> =
        workoutDao.getCompletedWorkouts().map { list ->
            list.map { entity -> loadWorkoutWithExercises(entity) }
        }
    
    override fun getActiveWorkoutFlow(): Flow<Workout?> =
        workoutDao.getActiveWorkoutFlow().map { entity ->
            entity?.let { loadWorkoutWithExercises(it) }
        }
    
    override fun getWorkoutByIdFlow(id: Long): Flow<Workout?> =
        workoutDao.getWorkoutByIdFlow(id).map { entity ->
            entity?.let { loadWorkoutWithExercises(it) }
        }
    
    override fun getCompletedWorkoutsCount(): Flow<Int> =
        workoutDao.getCompletedWorkoutsCount()
    
    override suspend fun getActiveWorkout(): Workout? =
        workoutDao.getActiveWorkout()?.let { loadWorkoutWithExercises(it) }
    
    override suspend fun getWorkoutById(id: Long): Workout? =
        workoutDao.getWorkoutById(id)?.let { loadWorkoutWithExercises(it) }
    
    override suspend fun getLastCompletedWorkout(): Workout? =
        workoutDao.getLastCompletedWorkout()?.let { loadWorkoutWithExercises(it) }
    
    override suspend fun startWorkout(): Long {
        val workout = WorkoutEntity(
            startedAt = Clock.System.now().toEpochMilliseconds()
        )
        return workoutDao.insertWorkout(workout)
    }
    
    override suspend fun finishWorkout(workoutId: Long, note: String?) {
        val workout = workoutDao.getWorkoutById(workoutId) ?: return
        workoutDao.updateWorkout(
            workout.copy(
                finishedAt = Clock.System.now().toEpochMilliseconds(),
                isCompleted = true,
                note = note
            )
        )
    }
    
    override suspend fun discardWorkout(workoutId: Long) {
        workoutDao.deleteWorkoutById(workoutId)
    }
    
    override suspend fun addExerciseToWorkout(workoutId: Long, exerciseId: Long): Long {
        val maxOrder = workoutExerciseDao.getMaxOrderForWorkout(workoutId) ?: -1
        val workoutExercise = WorkoutExerciseEntity(
            workoutId = workoutId,
            exerciseId = exerciseId,
            order = maxOrder + 1
        )
        return workoutExerciseDao.insertWorkoutExercise(workoutExercise)
    }
    
    override suspend fun removeExerciseFromWorkout(workoutExerciseId: Long) {
        workoutExerciseDao.deleteWorkoutExerciseById(workoutExerciseId)
    }
    
    override suspend fun addSet(
        workoutExerciseId: Long,
        weight: Float,
        reps: Int,
        rpe: Int?
    ): Long {
        val maxSetNumber = setDao.getMaxSetNumber(workoutExerciseId) ?: 0
        val set = SetEntity(
            workoutExerciseId = workoutExerciseId,
            setNumber = maxSetNumber + 1,
            weight = weight,
            reps = reps,
            rpe = rpe,
            isCompleted = true
        )
        return setDao.insertSet(set)
    }
    
    override suspend fun updateSet(set: ExerciseSet, workoutExerciseId: Long) {
        setDao.updateSet(set.toEntity(workoutExerciseId))
    }
    
    override suspend fun completeSet(setId: Long) {
        val set = setDao.getSetById(setId) ?: return
        setDao.updateSet(set.copy(isCompleted = true))
    }
    
    override suspend fun deleteSet(setId: Long) {
        setDao.deleteSetById(setId)
    }
    
    override suspend fun getLastPerformance(exerciseId: Long, limit: Int): List<WorkoutExercise> {
        val workoutExercises = workoutExerciseDao.getLastPerformances(exerciseId, limit)
        return workoutExercises.mapNotNull { we ->
            val exercise = exerciseDao.getExerciseById(we.exerciseId) ?: return@mapNotNull null
            val sets = setDao.getSetsForWorkoutExerciseSync(we.id)
            we.toDomain(exercise.toDomain(), sets.map { it.toDomain() })
        }
    }
    
    override suspend fun getExercisesForWorkout(workoutId: Long): List<WorkoutExercise> {
        val workoutExercises = workoutExerciseDao.getExercisesForWorkoutSync(workoutId)
        return workoutExercises.mapNotNull { we ->
            val exercise = exerciseDao.getExerciseById(we.exerciseId) ?: return@mapNotNull null
            val sets = setDao.getSetsForWorkoutExerciseSync(we.id)
            we.toDomain(exercise.toDomain(), sets.map { it.toDomain() })
        }
    }
    
    private suspend fun loadWorkoutWithExercises(entity: WorkoutEntity): Workout {
        val exercises = getExercisesForWorkout(entity.id)
        return entity.toDomain(exercises)
    }
}
