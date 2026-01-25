package com.example.stracker.di

import android.content.Context
import androidx.room.Room
import com.example.stracker.data.local.dao.ExerciseDao
import com.example.stracker.data.local.dao.SetDao
import com.example.stracker.data.local.dao.WorkoutDao
import com.example.stracker.data.local.dao.WorkoutExerciseDao
import com.example.stracker.data.local.db.STrackerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): STrackerDatabase {
        return Room.databaseBuilder(
            context,
            STrackerDatabase::class.java,
            "stracker_database"
        ).build()
    }
    
    @Provides
    fun provideExerciseDao(database: STrackerDatabase): ExerciseDao {
        return database.exerciseDao()
    }
    
    @Provides
    fun provideWorkoutDao(database: STrackerDatabase): WorkoutDao {
        return database.workoutDao()
    }
    
    @Provides
    fun provideWorkoutExerciseDao(database: STrackerDatabase): WorkoutExerciseDao {
        return database.workoutExerciseDao()
    }
    
    @Provides
    fun provideSetDao(database: STrackerDatabase): SetDao {
        return database.setDao()
    }
}
