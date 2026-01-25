package com.example.stracker.presentation.exercise.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.model.WorkoutExercise
import com.example.stracker.domain.repository.ExerciseRepository
import com.example.stracker.domain.repository.WorkoutRepository
import com.example.stracker.domain.usecase.progression.CalculateE1RMUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseDetailState(
    val exercise: Exercise? = null,
    val performances: List<WorkoutExercise> = emptyList(),
    val e1rmHistory: List<Float> = emptyList(),
    val averageE1RM: Float = 0f,
    val trendPercent: Float = 0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    private val calculateE1RMUseCase: CalculateE1RMUseCase
) : ViewModel() {
    
    private val exerciseId: Long = savedStateHandle.get<Long>("exerciseId") ?: 0L
    
    private val _state = MutableStateFlow(ExerciseDetailState())
    val state: StateFlow<ExerciseDetailState> = _state.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val exercise = exerciseRepository.getExerciseById(exerciseId)
                val performances = workoutRepository.getLastPerformance(exerciseId, limit = 10)
                
                // Calculate e1RM for each performance
                val e1rmHistory = performances.map { we ->
                    calculateE1RMUseCase.calculateBestE1RM(we.sets)
                }.filter { it > 0 }.reversed() // Oldest first for chart
                
                val averageE1RM = if (e1rmHistory.isNotEmpty()) {
                    e1rmHistory.average().toFloat()
                } else 0f
                
                val trendPercent = if (e1rmHistory.size >= 2) {
                    val oldest = e1rmHistory.first()
                    val newest = e1rmHistory.last()
                    ((newest - oldest) / oldest) * 100
                } else 0f
                
                _state.update { state ->
                    state.copy(
                        exercise = exercise,
                        performances = performances,
                        e1rmHistory = e1rmHistory,
                        averageE1RM = averageE1RM,
                        trendPercent = trendPercent,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
