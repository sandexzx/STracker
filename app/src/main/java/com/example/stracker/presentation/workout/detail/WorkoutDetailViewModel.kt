package com.example.stracker.presentation.workout.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stracker.domain.model.Workout
import com.example.stracker.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutDetailState(
    val workout: Workout? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    
    private val workoutId: Long = savedStateHandle.get<Long>("workoutId") ?: 0L
    
    private val _state = MutableStateFlow(WorkoutDetailState())
    val state: StateFlow<WorkoutDetailState> = _state.asStateFlow()
    
    init {
        loadWorkout()
    }
    
    private fun loadWorkout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val workout = workoutRepository.getWorkoutById(workoutId)
                _state.update { 
                    it.copy(
                        workout = workout, 
                        isLoading = false,
                        error = if (workout == null) "Тренировка не найдена" else null
                    ) 
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Ошибка загрузки"
                    ) 
                }
            }
        }
    }
}
