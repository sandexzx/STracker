package com.example.stracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stracker.domain.model.ExerciseCategory
import com.example.stracker.domain.model.MuscleGroup

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: ExerciseCategory,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: String = "", // JSON-сериализованный список
    val notes: String? = null,
    val isCustom: Boolean = false
)
