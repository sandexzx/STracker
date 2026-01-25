package com.example.stracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stracker.domain.repository.ExerciseRepository
import com.example.stracker.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    private val _navigateToWorkout = MutableSharedFlow<Long>()
    val navigateToWorkout: SharedFlow<Long> = _navigateToWorkout.asSharedFlow()
    
    init {
        viewModelScope.launch {
            exerciseRepository.initializeDefaultExercises()
        }
        loadData()
    }
    
    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.StartWorkout -> startNewWorkout()
            HomeEvent.ContinueWorkout -> continueWorkout()
            HomeEvent.Refresh -> loadData()
        }
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                // Подписываемся на активную тренировку
                combine(
                    workoutRepository.getActiveWorkoutFlow(),
                    workoutRepository.getCompletedWorkoutsCount(),
                    workoutRepository.getCompletedWorkouts()
                ) { activeWorkout, totalWorkouts, completedWorkouts ->
                    val lastWorkout = completedWorkouts.firstOrNull()
                    val totalExercises = completedWorkouts.sumOf { it.totalExercises }
                    val activeDays = completedWorkouts.map { 
                        it.startedAt.toEpochMilliseconds() / (24 * 60 * 60 * 1000) 
                    }.distinct().size
                    
                    _state.update { state ->
                        state.copy(
                            activeWorkout = activeWorkout,
                            lastWorkout = lastWorkout,
                            totalWorkouts = totalWorkouts,
                            totalExercises = totalExercises,
                            activeDays = activeDays,
                            isLoading = false,
                            error = null
                        )
                    }
                }.collect()
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Произошла ошибка"
                    ) 
                }
            }
        }
    }
    
    private fun startNewWorkout() {
        viewModelScope.launch {
            try {
                val workoutId = workoutRepository.startWorkout()
                _navigateToWorkout.emit(workoutId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun continueWorkout() {
        viewModelScope.launch {
            _state.value.activeWorkout?.let { workout ->
                _navigateToWorkout.emit(workout.id)
            }
        }
    }
}
