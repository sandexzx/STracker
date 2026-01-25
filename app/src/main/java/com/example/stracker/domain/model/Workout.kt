package com.example.stracker.domain.model

import kotlinx.datetime.Instant

data class Workout(
    val id: Long = 0,
    val startedAt: Instant,
    val finishedAt: Instant? = null,
    val exercises: List<WorkoutExercise> = emptyList(),
    val note: String? = null,
    val isCompleted: Boolean = false
) {
    val durationMinutes: Long
        get() {
            val endTime = finishedAt ?: return 0
            return (endTime.toEpochMilliseconds() - startedAt.toEpochMilliseconds()) / 60000
        }
    
    val totalExercises: Int
        get() = exercises.size
    
    val totalSets: Int
        get() = exercises.sumOf { it.sets.count { set -> set.isCompleted } }
    
    val totalVolume: Float
        get() = exercises.sumOf { it.totalVolume.toDouble() }.toFloat()
}
