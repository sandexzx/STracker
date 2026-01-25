package com.example.stracker.domain.usecase.progression

import com.example.stracker.domain.model.ExerciseCategory
import com.example.stracker.domain.model.ProgressionAdvice
import com.example.stracker.domain.model.ProgressionTrend
import com.example.stracker.domain.model.WorkoutExercise
import com.example.stracker.domain.repository.WorkoutRepository
import javax.inject.Inject

class GetProgressionAdviceUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val calculateE1RMUseCase: CalculateE1RMUseCase
) {
    
    suspend operator fun invoke(exerciseId: Long, category: ExerciseCategory): ProgressionAdvice {
        val lastPerformances = workoutRepository.getLastPerformance(exerciseId, limit = 5)
        
        if (lastPerformances.isEmpty()) {
            return ProgressionAdvice(
                recommendedWeight = 0f,
                recommendedReps = 8,
                trend = ProgressionTrend.FIRST_TIME,
                message = "Первое выполнение. Начните с комфортного веса."
            )
        }
        
        // Рассчитываем e1RM для каждой сессии
        val e1rmValues = lastPerformances.map { we ->
            calculateE1RMUseCase.calculateBestE1RM(we.sets)
        }.filter { it > 0 }
        
        if (e1rmValues.isEmpty()) {
            return createFirstTimeAdvice(lastPerformances.first())
        }
        
        val lastPerformance = lastPerformances.first()
        val lastWeight = lastPerformance.sets.filter { it.isCompleted && !it.isWarmup }
            .maxOfOrNull { it.weight } ?: 0f
        val lastReps = lastPerformance.sets.filter { it.isCompleted && !it.isWarmup }
            .maxOfOrNull { it.reps } ?: 8
        val lastRpe = lastPerformance.sets.filter { it.isCompleted && !it.isWarmup }
            .lastOrNull()?.rpe ?: 7
        
        // Определяем тренд
        val trend = calculateTrend(e1rmValues)
        val progressionStep = category.progressionStep
        
        return when (trend) {
            ProgressionTrend.PROGRESSING -> {
                // RPE проверка: если последний подход был на RPE >= 9, не прибавляем
                if (lastRpe >= 9) {
                    ProgressionAdvice(
                        recommendedWeight = lastWeight,
                        recommendedReps = lastReps,
                        trend = ProgressionTrend.PLATEAU,
                        message = "RPE высокий. Закрепите результат на $lastWeight кг."
                    )
                } else {
                    ProgressionAdvice(
                        recommendedWeight = lastWeight + progressionStep,
                        recommendedReps = lastReps,
                        trend = ProgressionTrend.PROGRESSING,
                        message = "Прогресс! +${progressionStep} кг → ${lastWeight + progressionStep} кг"
                    )
                }
            }
            ProgressionTrend.PLATEAU -> {
                ProgressionAdvice(
                    recommendedWeight = lastWeight,
                    recommendedReps = lastReps + 1,
                    trend = ProgressionTrend.PLATEAU,
                    message = "Плато. Попробуйте +1 повтор или deload."
                )
            }
            ProgressionTrend.REGRESSING -> {
                val deloadWeight = lastWeight * 0.9f
                ProgressionAdvice(
                    recommendedWeight = roundToStep(deloadWeight, progressionStep),
                    recommendedReps = lastReps,
                    trend = ProgressionTrend.REGRESSING,
                    message = "Регрессия. Снизьте вес на 10% до ${roundToStep(deloadWeight, progressionStep)} кг."
                )
            }
            ProgressionTrend.FIRST_TIME -> {
                createFirstTimeAdvice(lastPerformance)
            }
        }
    }
    
    private fun calculateTrend(e1rmValues: List<Float>): ProgressionTrend {
        if (e1rmValues.size < 2) return ProgressionTrend.FIRST_TIME
        
        val recentValues = e1rmValues.take(3)
        val avgRecent = recentValues.average().toFloat()
        val oldest = recentValues.last()
        
        val percentChange = ((avgRecent - oldest) / oldest) * 100
        
        return when {
            percentChange >= 2.5f -> ProgressionTrend.PROGRESSING
            percentChange <= -5f -> ProgressionTrend.REGRESSING
            else -> ProgressionTrend.PLATEAU
        }
    }
    
    private fun createFirstTimeAdvice(lastPerformance: WorkoutExercise): ProgressionAdvice {
        val lastWeight = lastPerformance.sets.firstOrNull()?.weight ?: 0f
        val lastReps = lastPerformance.sets.firstOrNull()?.reps ?: 8
        return ProgressionAdvice(
            recommendedWeight = lastWeight,
            recommendedReps = lastReps,
            trend = ProgressionTrend.FIRST_TIME,
            message = "Повторите предыдущий результат."
        )
    }
    
    private fun roundToStep(value: Float, step: Float): Float {
        return (value / step).toInt() * step
    }
}
