package com.example.stracker.domain.usecase.progression

import com.example.stracker.domain.model.ExerciseSet
import javax.inject.Inject

enum class E1RMFormula {
    EPLEY, BRZYCKI
}

class CalculateE1RMUseCase @Inject constructor() {
    
    operator fun invoke(
        weight: Float, 
        reps: Int, 
        formula: E1RMFormula = E1RMFormula.EPLEY
    ): Float {
        if (reps == 0) return 0f
        return when (formula) {
            E1RMFormula.EPLEY -> weight * (1 + reps / 30f)
            E1RMFormula.BRZYCKI -> {
                if (reps >= 37) 0f
                else weight * (36f / (37 - reps))
            }
        }
    }
    
    fun calculateForSet(set: ExerciseSet, formula: E1RMFormula = E1RMFormula.EPLEY): Float {
        return invoke(set.weight, set.reps, formula)
    }
    
    fun calculateBestE1RM(sets: List<ExerciseSet>, formula: E1RMFormula = E1RMFormula.EPLEY): Float {
        return sets.filter { it.isCompleted && !it.isWarmup }
            .maxOfOrNull { calculateForSet(it, formula) } ?: 0f
    }
}
