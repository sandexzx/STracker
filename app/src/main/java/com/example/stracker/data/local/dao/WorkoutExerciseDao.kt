package com.example.stracker.data.local.dao

import androidx.room.*
import com.example.stracker.data.local.entity.WorkoutExerciseEntity
import kotlinx.coroutines.flow.Flow

data class WorkoutExerciseWithExercise(
    val workoutExercise: WorkoutExerciseEntity,
    val exerciseId: Long,
    val exerciseName: String
)

@Dao
interface WorkoutExerciseDao {
    
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY `order` ASC")
    fun getExercisesForWorkout(workoutId: Long): Flow<List<WorkoutExerciseEntity>>
    
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY `order` ASC")
    suspend fun getExercisesForWorkoutSync(workoutId: Long): List<WorkoutExerciseEntity>
    
    @Query("SELECT * FROM workout_exercises WHERE id = :id")
    suspend fun getWorkoutExerciseById(id: Long): WorkoutExerciseEntity?
    
    @Query("""
        SELECT we.* FROM workout_exercises we
        INNER JOIN workouts w ON we.workoutId = w.id
        WHERE we.exerciseId = :exerciseId AND w.isCompleted = 1
        ORDER BY w.startedAt DESC
        LIMIT :limit
    """)
    suspend fun getLastPerformances(exerciseId: Long, limit: Int = 5): List<WorkoutExerciseEntity>
    
    @Query("SELECT MAX(`order`) FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun getMaxOrderForWorkout(workoutId: Long): Int?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity): Long
    
    @Update
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExerciseEntity)
    
    @Delete
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExerciseEntity)
    
    @Query("DELETE FROM workout_exercises WHERE id = :id")
    suspend fun deleteWorkoutExerciseById(id: Long)
}
