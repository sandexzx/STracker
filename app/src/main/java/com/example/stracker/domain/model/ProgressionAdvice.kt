package com.example.stracker.domain.model

data class ProgressionAdvice(
    val recommendedWeight: Float,
    val recommendedReps: Int,
    val trend: ProgressionTrend,
    val message: String
)

enum class ProgressionTrend {
    PROGRESSING,    // Можно прибавлять
    PLATEAU,        // Стабильно, закрепляем
    REGRESSING,     // Снижение, нужен отдых/deload
    FIRST_TIME      // Первое выполнение упражнения
}
