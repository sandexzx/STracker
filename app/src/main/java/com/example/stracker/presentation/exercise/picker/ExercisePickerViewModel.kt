package com.example.stracker.presentation.exercise.picker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.model.MuscleGroup
import com.example.stracker.domain.usecase.exercise.ExerciseUseCases
import com.example.stracker.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExercisePickerState(
    val exercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscleGroup: MuscleGroup? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ExercisePickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val exerciseUseCases: ExerciseUseCases,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    
    private val workoutId: Long = savedStateHandle.get<Long>("workoutId") ?: 0L
    
    private val _state = MutableStateFlow(ExercisePickerState())
    val state: StateFlow<ExercisePickerState> = _state.asStateFlow()
    
    private val _exerciseAdded = MutableSharedFlow<Unit>()
    val exerciseAdded: SharedFlow<Unit> = _exerciseAdded.asSharedFlow()
    
    init {
        loadExercises()
    }
    
    private fun loadExercises() {
        viewModelScope.launch {
            exerciseUseCases.getExercises().collect { exercises ->
                _state.update { state ->
                    state.copy(
                        exercises = exercises,
                        filteredExercises = filterExercises(exercises, state.searchQuery, state.selectedMuscleGroup),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _state.update { state ->
            state.copy(
                searchQuery = query,
                filteredExercises = filterExercises(state.exercises, query, state.selectedMuscleGroup)
            )
        }
    }
    
    fun onMuscleGroupSelect(muscleGroup: MuscleGroup?) {
        _state.update { state ->
            state.copy(
                selectedMuscleGroup = muscleGroup,
                filteredExercises = filterExercises(state.exercises, state.searchQuery, muscleGroup)
            )
        }
    }
    
    fun onExerciseSelect(exerciseId: Long) {
        viewModelScope.launch {
            try {
                workoutRepository.addExerciseToWorkout(workoutId, exerciseId)
                _exerciseAdded.emit(Unit)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private fun filterExercises(
        exercises: List<Exercise>,
        query: String,
        muscleGroup: MuscleGroup?
    ): List<Exercise> {
        return exercises.filter { exercise ->
            val matchesQuery = query.isBlank() || 
                exercise.name.contains(query, ignoreCase = true)
            val matchesMuscle = muscleGroup == null || 
                exercise.primaryMuscle == muscleGroup
            matchesQuery && matchesMuscle
        }
    }
}
