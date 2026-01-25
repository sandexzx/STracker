package com.example.stracker.data.local.dao

import androidx.room.*
import com.example.stracker.data.local.entity.SetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    
    @Query("SELECT * FROM sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    fun getSetsForWorkoutExercise(workoutExerciseId: Long): Flow<List<SetEntity>>
    
    @Query("SELECT * FROM sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    suspend fun getSetsForWorkoutExerciseSync(workoutExerciseId: Long): List<SetEntity>
    
    @Query("SELECT * FROM sets WHERE id = :id")
    suspend fun getSetById(id: Long): SetEntity?
    
    @Query("SELECT MAX(setNumber) FROM sets WHERE workoutExerciseId = :workoutExerciseId")
    suspend fun getMaxSetNumber(workoutExerciseId: Long): Int?
    
    @Query("SELECT COUNT(*) FROM sets WHERE workoutExerciseId = :workoutExerciseId AND isCompleted = 1")
    suspend fun getCompletedSetsCount(workoutExerciseId: Long): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<SetEntity>)
    
    @Update
    suspend fun updateSet(set: SetEntity)
    
    @Delete
    suspend fun deleteSet(set: SetEntity)
    
    @Query("DELETE FROM sets WHERE id = :id")
    suspend fun deleteSetById(id: Long)
}
