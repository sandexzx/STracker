package com.example.stracker.domain.usecase.exercise

import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.repository.ExerciseRepository
import javax.inject.Inject

class DeleteExerciseUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(exercise: Exercise) {
        // We only allow deleting custom exercises? 
        // Or we should check if it's used in any workouts?
        // For now, let's just delete.
        repository.deleteExercise(exercise)
    }
}
