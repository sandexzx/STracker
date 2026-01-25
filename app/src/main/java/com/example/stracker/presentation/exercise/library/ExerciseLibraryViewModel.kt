package com.example.stracker.presentation.exercise.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.model.ExerciseCategory
import com.example.stracker.domain.model.MuscleGroup
import com.example.stracker.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseLibraryState(
    val exercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscleGroup: MuscleGroup? = null,
    val isLoading: Boolean = true
) {
    val compoundExercises: List<Exercise>
        get() = filteredExercises.filter { it.category == ExerciseCategory.COMPOUND }
    
    val accessoryExercises: List<Exercise>
        get() = filteredExercises.filter { it.category == ExerciseCategory.ACCESSORY }
    
    val isolationExercises: List<Exercise>
        get() = filteredExercises.filter { it.category == ExerciseCategory.ISOLATION }
}

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExerciseLibraryState())
    val state: StateFlow<ExerciseLibraryState> = _state.asStateFlow()
    
    init {
        loadExercises()
    }
    
    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
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
