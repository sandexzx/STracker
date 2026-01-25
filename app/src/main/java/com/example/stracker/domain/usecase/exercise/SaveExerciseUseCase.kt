package com.example.stracker.domain.usecase.exercise

import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.repository.ExerciseRepository
import javax.inject.Inject

class SaveExerciseUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(exercise: Exercise) {
        if (exercise.id == 0L) {
            repository.insertExercise(exercise)
        } else {
            repository.updateExercise(exercise)
        }
    }
}
