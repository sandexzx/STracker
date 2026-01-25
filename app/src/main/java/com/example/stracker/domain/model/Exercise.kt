package com.example.stracker.domain.model

data class Exercise(
    val id: Long = 0,
    val name: String,
    val category: ExerciseCategory,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val notes: String? = null,
    val isCustom: Boolean = false
)
