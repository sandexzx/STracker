package com.example.stracker.domain.model

enum class ExerciseCategory(val displayName: String, val progressionStep: Float) {
    COMPOUND("Базовое", 2.5f),
    ACCESSORY("Вспомогательное", 1.25f),
    ISOLATION("Изолирующее", 1.25f)
}
