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

enum class HistoryFilter(val displayName: String) {
    ALL("Все"),
    THIS_WEEK("Эта неделя"),
    THIS_MONTH("Этот месяц"),
    LAST_30_DAYS("30 дней")
}

data class WorkoutHistoryState(
    val workouts: List<Workout> = emptyList(),
    val filter: HistoryFilter = HistoryFilter.ALL,
    val showFilterDialog: Boolean = false,
    val isLoading: Boolean = true
) {
    private val filteredWorkouts: List<Workout>
        get() {
            val now = System.currentTimeMillis()
            return when (filter) {
                HistoryFilter.ALL -> workouts
                HistoryFilter.THIS_WEEK -> {
                    val weekAgo = now - 7 * 24 * 60 * 60 * 1000
                    workouts.filter { it.startedAt.toEpochMilliseconds() >= weekAgo }
                }
                HistoryFilter.THIS_MONTH -> {
                    val monthAgo = now - 30 * 24 * 60 * 60 * 1000
                    workouts.filter { it.startedAt.toEpochMilliseconds() >= monthAgo }
                }
                HistoryFilter.LAST_30_DAYS -> {
                    val thirtyDaysAgo = now - 30 * 24 * 60 * 60 * 1000
                    workouts.filter { it.startedAt.toEpochMilliseconds() >= thirtyDaysAgo }
                }
            }
        }
    
    val thisWeekWorkouts: List<Workout>
        get() {
            val now = System.currentTimeMillis()
            val weekAgo = now - 7 * 24 * 60 * 60 * 1000
            return filteredWorkouts.filter { it.startedAt.toEpochMilliseconds() >= weekAgo }
        }
    
    val earlierWorkouts: List<Workout>
        get() {
            val weekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
            return filteredWorkouts.filter { it.startedAt.toEpochMilliseconds() < weekAgo }
        }
}

sealed class WorkoutHistoryEvent {
    data class SetFilter(val filter: HistoryFilter) : WorkoutHistoryEvent()
    data object ShowFilterDialog : WorkoutHistoryEvent()
    data object HideFilterDialog : WorkoutHistoryEvent()
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
    
    fun onEvent(event: WorkoutHistoryEvent) {
        when (event) {
            is WorkoutHistoryEvent.SetFilter -> {
                _state.update { it.copy(filter = event.filter, showFilterDialog = false) }
            }
            WorkoutHistoryEvent.ShowFilterDialog -> {
                _state.update { it.copy(showFilterDialog = true) }
            }
            WorkoutHistoryEvent.HideFilterDialog -> {
                _state.update { it.copy(showFilterDialog = false) }
            }
        }
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
