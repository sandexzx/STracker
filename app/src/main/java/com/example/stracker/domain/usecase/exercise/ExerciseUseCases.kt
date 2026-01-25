package com.example.stracker.domain.usecase.exercise

data class ExerciseUseCases(
    val getExercises: GetExercisesUseCase,
    val getExercise: GetExerciseUseCase,
    val saveExercise: SaveExerciseUseCase,
    val deleteExercise: DeleteExerciseUseCase
)
