package com.example.stracker.data.local.db

import androidx.room.TypeConverter
import com.example.stracker.domain.model.ExerciseCategory
import com.example.stracker.domain.model.MuscleGroup

class Converters {
    
    @TypeConverter
    fun fromExerciseCategory(category: ExerciseCategory): String = category.name
    
    @TypeConverter
    fun toExerciseCategory(value: String): ExerciseCategory = ExerciseCategory.valueOf(value)
    
    @TypeConverter
    fun fromMuscleGroup(muscleGroup: MuscleGroup): String = muscleGroup.name
    
    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup = MuscleGroup.valueOf(value)
    
    @TypeConverter
    fun fromMuscleGroupList(list: List<MuscleGroup>): String = list.joinToString(",") { it.name }
    
    @TypeConverter
    fun toMuscleGroupList(value: String): List<MuscleGroup> {
        if (value.isBlank()) return emptyList()
        return value.split(",").map { MuscleGroup.valueOf(it) }
    }
}
