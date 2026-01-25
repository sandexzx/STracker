package com.example.stracker.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stracker.data.local.dao.ExerciseDao
import com.example.stracker.data.local.dao.SetDao
import com.example.stracker.data.local.dao.WorkoutDao
import com.example.stracker.data.local.dao.WorkoutExerciseDao
import com.example.stracker.data.local.entity.ExerciseEntity
import com.example.stracker.data.local.entity.SetEntity
import com.example.stracker.data.local.entity.WorkoutEntity
import com.example.stracker.data.local.entity.WorkoutExerciseEntity

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        SetEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class STrackerDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun setDao(): SetDao
}
