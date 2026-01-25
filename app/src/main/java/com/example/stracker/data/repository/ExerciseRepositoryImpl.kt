package com.example.stracker.data.repository

import com.example.stracker.data.local.dao.ExerciseDao
import com.example.stracker.data.local.entity.ExerciseEntity
import com.example.stracker.data.mapper.toDomain
import com.example.stracker.data.mapper.toEntity
import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.model.ExerciseCategory
import com.example.stracker.domain.model.MuscleGroup
import com.example.stracker.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {
    
    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAllExercises().map { list -> list.map { it.toDomain() } }
    
    override fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>> =
        exerciseDao.getExercisesByMuscleGroup(muscleGroup).map { list -> list.map { it.toDomain() } }
    
    override fun searchExercises(query: String): Flow<List<Exercise>> =
        exerciseDao.searchExercises(query).map { list -> list.map { it.toDomain() } }
    
    override suspend fun getExerciseById(id: Long): Exercise? =
        exerciseDao.getExerciseById(id)?.toDomain()
    
    override suspend fun insertExercise(exercise: Exercise): Long =
        exerciseDao.insertExercise(exercise.toEntity(isCustom = true))
    
    override suspend fun updateExercise(exercise: Exercise) =
        exerciseDao.updateExercise(exercise.toEntity())
    
    override suspend fun deleteExercise(exercise: Exercise) =
        exerciseDao.deleteExercise(exercise.toEntity())
    
    override suspend fun initializeDefaultExercises() {
        if (exerciseDao.getExerciseCount() > 0) return
        
        val defaultExercises = listOf(
            // Грудь
            ExerciseEntity(name = "Жим штанги лёжа", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.CHEST, secondaryMuscles = "TRICEPS,SHOULDERS"),
            ExerciseEntity(name = "Жим гантелей лёжа", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.CHEST, secondaryMuscles = "TRICEPS,SHOULDERS"),
            ExerciseEntity(name = "Жим на наклонной скамье", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.CHEST, secondaryMuscles = "TRICEPS,SHOULDERS"),
            ExerciseEntity(name = "Разводка гантелей", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.CHEST),
            ExerciseEntity(name = "Сведение в кроссовере", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.CHEST),
            
            // Спина
            ExerciseEntity(name = "Становая тяга", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.BACK, secondaryMuscles = "HAMSTRINGS,GLUTES"),
            ExerciseEntity(name = "Подтягивания", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.BACK, secondaryMuscles = "BICEPS"),
            ExerciseEntity(name = "Тяга штанги в наклоне", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.BACK, secondaryMuscles = "BICEPS"),
            ExerciseEntity(name = "Тяга гантели одной рукой", category = ExerciseCategory.ACCESSORY, primaryMuscle = MuscleGroup.BACK, secondaryMuscles = "BICEPS"),
            ExerciseEntity(name = "Тяга верхнего блока", category = ExerciseCategory.ACCESSORY, primaryMuscle = MuscleGroup.BACK, secondaryMuscles = "BICEPS"),
            
            // Ноги
            ExerciseEntity(name = "Приседания со штангой", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.QUADRICEPS, secondaryMuscles = "GLUTES,HAMSTRINGS"),
            ExerciseEntity(name = "Жим ногами", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.QUADRICEPS, secondaryMuscles = "GLUTES"),
            ExerciseEntity(name = "Румынская тяга", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.HAMSTRINGS, secondaryMuscles = "GLUTES,BACK"),
            ExerciseEntity(name = "Выпады", category = ExerciseCategory.ACCESSORY, primaryMuscle = MuscleGroup.QUADRICEPS, secondaryMuscles = "GLUTES"),
            ExerciseEntity(name = "Сгибание ног", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.HAMSTRINGS),
            ExerciseEntity(name = "Разгибание ног", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.QUADRICEPS),
            
            // Плечи
            ExerciseEntity(name = "Жим штанги стоя", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.SHOULDERS, secondaryMuscles = "TRICEPS"),
            ExerciseEntity(name = "Жим гантелей сидя", category = ExerciseCategory.COMPOUND, primaryMuscle = MuscleGroup.SHOULDERS, secondaryMuscles = "TRICEPS"),
            ExerciseEntity(name = "Махи гантелей в стороны", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.SHOULDERS),
            ExerciseEntity(name = "Тяга к подбородку", category = ExerciseCategory.ACCESSORY, primaryMuscle = MuscleGroup.SHOULDERS, secondaryMuscles = "BICEPS"),
            
            // Руки
            ExerciseEntity(name = "Сгибание штанги на бицепс", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.BICEPS),
            ExerciseEntity(name = "Молотки", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.BICEPS, secondaryMuscles = "FOREARMS"),
            ExerciseEntity(name = "Французский жим", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.TRICEPS),
            ExerciseEntity(name = "Разгибание на трицепс", category = ExerciseCategory.ISOLATION, primaryMuscle = MuscleGroup.TRICEPS)
        )
        
        exerciseDao.insertExercises(defaultExercises)
    }
}
