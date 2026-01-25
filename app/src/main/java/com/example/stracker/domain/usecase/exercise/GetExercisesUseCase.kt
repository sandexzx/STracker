package com.example.stracker.domain.usecase.exercise

import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    operator fun invoke(): Flow<List<Exercise>> {
        return repository.getAllExercises()
    }
}
