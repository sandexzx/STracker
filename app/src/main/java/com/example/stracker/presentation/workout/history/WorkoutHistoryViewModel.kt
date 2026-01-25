package com.example.stracker.presentation.workout.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stracker.domain.model.Workout
import com.example.stracker.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

data class WorkoutHistoryState(
    val workouts: List<Workout> = emptyList(),
    val isLoading: Boolean = true
) {
    // Group by week
    val thisWeekWorkouts: List<Workout>
        get() {
            val now = System.currentTimeMillis()
            val weekAgo = now - 7 * 24 * 60 * 60 * 1000
            return workouts.filter { it.startedAt.toEpochMilliseconds() >= weekAgo }
        }
    
    val earlierWorkouts: List<Workout>
        get() {
            val weekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
            return workouts.filter { it.startedAt.toEpochMilliseconds() < weekAgo }
        }
}

@HiltViewModel
class WorkoutHistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(WorkoutHistoryState())
    val state: StateFlow<WorkoutHistoryState> = _state.asStateFlow()
    
    init {
        loadWorkouts()
    }
    
    private fun loadWorkouts() {
        viewModelScope.launch {
            workoutRepository.getCompletedWorkouts().collect { workouts ->
                _state.update { state ->
                    state.copy(
                        workouts = workouts,
                        isLoading = false
                    )
                }
            }
        }
    }
}
