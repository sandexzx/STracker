package com.example.stracker.di

import com.example.stracker.domain.repository.ExerciseRepository
import com.example.stracker.domain.usecase.exercise.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideExerciseUseCases(repository: ExerciseRepository): ExerciseUseCases {
        return ExerciseUseCases(
            getExercises = GetExercisesUseCase(repository),
            getExercise = GetExerciseUseCase(repository),
            saveExercise = SaveExerciseUseCase(repository),
            deleteExercise = DeleteExerciseUseCase(repository)
        )
    }
}
