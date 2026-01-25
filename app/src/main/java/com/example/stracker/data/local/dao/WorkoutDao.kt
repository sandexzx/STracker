package com.example.stracker.data.local.dao

import androidx.room.*
import com.example.stracker.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    
    @Query("SELECT * FROM workouts ORDER BY startedAt DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>
    
    @Query("SELECT * FROM workouts WHERE isCompleted = 1 ORDER BY startedAt DESC")
    fun getCompletedWorkouts(): Flow<List<WorkoutEntity>>
    
    @Query("SELECT * FROM workouts WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveWorkout(): WorkoutEntity?
    
    @Query("SELECT * FROM workouts WHERE isCompleted = 0 LIMIT 1")
    fun getActiveWorkoutFlow(): Flow<WorkoutEntity?>
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutEntity?
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutByIdFlow(id: Long): Flow<WorkoutEntity?>
    
    @Query("SELECT * FROM workouts WHERE isCompleted = 1 ORDER BY startedAt DESC LIMIT 1")
    suspend fun getLastCompletedWorkout(): WorkoutEntity?
    
    @Query("SELECT * FROM workouts WHERE isCompleted = 1 ORDER BY startedAt DESC LIMIT 1")
    fun getLastCompletedWorkoutFlow(): Flow<WorkoutEntity?>
    
    @Query("SELECT COUNT(*) FROM workouts WHERE isCompleted = 1")
    fun getCompletedWorkoutsCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long
    
    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)
    
    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
    
    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkoutById(id: Long)
}
