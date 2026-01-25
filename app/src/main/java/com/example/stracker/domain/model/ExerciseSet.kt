package com.example.stracker.domain.model

data class ExerciseSet(
    val id: Long = 0,
    val setNumber: Int,
    val weight: Float,
    val reps: Int,
    val rpe: Int? = null,
    val isCompleted: Boolean = false,
    val isWarmup: Boolean = false
) {
    /**
     * Рассчитывает e1RM по формуле Epley
     */
    fun calculateE1RMEpley(): Float {
        if (reps == 0) return 0f
        return weight * (1 + reps / 30f)
    }

    /**
     * Рассчитывает e1RM по формуле Brzycki
     */
    fun calculateE1RMBrzycki(): Float {
        if (reps == 0 || reps >= 37) return 0f
        return weight * (36f / (37 - reps))
    }
}
