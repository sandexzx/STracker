package com.example.stracker.data.mapper

import com.example.stracker.data.local.entity.*
import com.example.stracker.domain.model.*
import kotlinx.datetime.Instant

// Exercise Mappers
fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    category = category,
    primaryMuscle = primaryMuscle,
    secondaryMuscles = if (secondaryMuscles.isBlank()) emptyList() 
        else secondaryMuscles.split(",").map { MuscleGroup.valueOf(it) },
    notes = notes,
    isCustom = isCustom
)

fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(
    id = id,
    name = name,
    category = category,
    primaryMuscle = primaryMuscle,
    secondaryMuscles = secondaryMuscles.joinToString(",") { it.name },
    notes = notes,
    isCustom = isCustom
)

// Set Mappers
fun SetEntity.toDomain(): ExerciseSet = ExerciseSet(
    id = id,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    rpe = rpe,
    isCompleted = isCompleted,
    isWarmup = isWarmup
)

fun ExerciseSet.toEntity(workoutExerciseId: Long): SetEntity = SetEntity(
    id = id,
    workoutExerciseId = workoutExerciseId,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    rpe = rpe,
    isCompleted = isCompleted,
    isWarmup = isWarmup
)

// Workout Mappers
fun WorkoutEntity.toDomain(exercises: List<WorkoutExercise> = emptyList()): Workout = Workout(
    id = id,
    startedAt = Instant.fromEpochMilliseconds(startedAt),
    finishedAt = finishedAt?.let { Instant.fromEpochMilliseconds(it) },
    exercises = exercises,
    note = note,
    isCompleted = isCompleted
)

fun Workout.toEntity(): WorkoutEntity = WorkoutEntity(
    id = id,
    startedAt = startedAt.toEpochMilliseconds(),
    finishedAt = finishedAt?.toEpochMilliseconds(),
    note = note,
    isCompleted = isCompleted
)

// WorkoutExercise Mappers
fun WorkoutExerciseEntity.toDomain(exercise: Exercise, sets: List<ExerciseSet>): WorkoutExercise = WorkoutExercise(
    id = id,
    exercise = exercise,
    sets = sets,
    order = order,
    restSeconds = restSeconds
)

fun WorkoutExercise.toEntity(workoutId: Long): WorkoutExerciseEntity = WorkoutExerciseEntity(
    id = id,
    workoutId = workoutId,
    exerciseId = exercise.id,
    order = order,
    restSeconds = restSeconds
)

fun WorkoutExerciseWithSets.toDomain(): WorkoutExercise = workoutExercise.toDomain(
    exercise = exercise.toDomain(),
    sets = sets.map { it.toDomain() }
)

fun WorkoutWithExercises.toDomain(): Workout = workout.toDomain(
    exercises = exercises.map { it.toDomain() }.sortedBy { it.order }
)
