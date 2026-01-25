package com.example.stracker.domain.model

data class WorkoutExercise(
    val id: Long = 0,
    val exercise: Exercise,
    val sets: List<ExerciseSet> = emptyList(),
    val order: Int,
    val restSeconds: Int? = null
) {
    val completedSetsCount: Int
        get() = sets.count { it.isCompleted }
    
    val totalVolume: Float
        get() = sets.filter { it.isCompleted && !it.isWarmup }
            .sumOf { (it.weight * it.reps).toDouble() }.toFloat()
    
    val bestE1RM: Float
        get() = sets.filter { it.isCompleted && !it.isWarmup }
            .maxOfOrNull { it.calculateE1RMEpley() } ?: 0f
}
