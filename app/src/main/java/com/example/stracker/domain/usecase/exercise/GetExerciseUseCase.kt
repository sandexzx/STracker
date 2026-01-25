package com.example.stracker.domain.usecase.exercise

import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.repository.ExerciseRepository
import javax.inject.Inject

class GetExerciseUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(id: Long): Exercise? {
        return repository.getExerciseById(id)
    }
}
