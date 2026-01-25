package com.example.stracker.presentation.exercise.create

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

data class CreateExerciseState(
    val name: String = "",
    val category: ExerciseCategory = ExerciseCategory.COMPOUND,
    val primaryMuscle: MuscleGroup = MuscleGroup.CHEST,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val nameError: String? = null
)

sealed class CreateExerciseEvent {
    data class UpdateName(val name: String) : CreateExerciseEvent()
    data class UpdateCategory(val category: ExerciseCategory) : CreateExerciseEvent()
    data class UpdatePrimaryMuscle(val muscle: MuscleGroup) : CreateExerciseEvent()
    data class ToggleSecondaryMuscle(val muscle: MuscleGroup) : CreateExerciseEvent()
    data class UpdateNotes(val notes: String) : CreateExerciseEvent()
    data object SaveExercise : CreateExerciseEvent()
}

@HiltViewModel
class CreateExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CreateExerciseState())
    val state: StateFlow<CreateExerciseState> = _state.asStateFlow()
    
    private val _exerciseCreated = MutableSharedFlow<Unit>()
    val exerciseCreated: SharedFlow<Unit> = _exerciseCreated.asSharedFlow()
    
    fun onEvent(event: CreateExerciseEvent) {
        when (event) {
            is CreateExerciseEvent.UpdateName -> {
                _state.update { it.copy(name = event.name, nameError = null) }
            }
            is CreateExerciseEvent.UpdateCategory -> {
                _state.update { it.copy(category = event.category) }
            }
            is CreateExerciseEvent.UpdatePrimaryMuscle -> {
                _state.update { it.copy(primaryMuscle = event.muscle) }
            }
            is CreateExerciseEvent.ToggleSecondaryMuscle -> {
                _state.update { state ->
                    val newList = if (event.muscle in state.secondaryMuscles) {
                        state.secondaryMuscles - event.muscle
                    } else {
                        state.secondaryMuscles + event.muscle
                    }
                    state.copy(secondaryMuscles = newList)
                }
            }
            is CreateExerciseEvent.UpdateNotes -> {
                _state.update { it.copy(notes = event.notes) }
            }
            CreateExerciseEvent.SaveExercise -> saveExercise()
        }
    }
    
    private fun saveExercise() {
        val currentState = _state.value
        
        if (currentState.name.isBlank()) {
            _state.update { it.copy(nameError = "Название не может быть пустым") }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val exercise = Exercise(
                    name = currentState.name.trim(),
                    category = currentState.category,
                    primaryMuscle = currentState.primaryMuscle,
                    secondaryMuscles = currentState.secondaryMuscles,
                    notes = currentState.notes.ifBlank { null }
                )
                exerciseRepository.insertExercise(exercise)
                _exerciseCreated.emit(Unit)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
