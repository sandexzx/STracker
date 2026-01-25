package com.example.stracker.presentation.workout.active

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stracker.domain.model.ExerciseSet
import com.example.stracker.domain.repository.WorkoutRepository
import com.example.stracker.domain.usecase.progression.GetProgressionAdviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository,
    private val getProgressionAdviceUseCase: GetProgressionAdviceUseCase
) : ViewModel() {
    
    private val workoutId: Long = savedStateHandle.get<Long>("workoutId") ?: 0L
    
    private val _state = MutableStateFlow(ActiveWorkoutState())
    val state: StateFlow<ActiveWorkoutState> = _state.asStateFlow()
    
    private val _workoutFinished = MutableSharedFlow<Unit>()
    val workoutFinished: SharedFlow<Unit> = _workoutFinished.asSharedFlow()
    
    init {
        loadWorkout()
        startTimer()
    }
    
    fun onEvent(event: ActiveWorkoutEvent) {
        when (event) {
            is ActiveWorkoutEvent.AddExercise -> { /* Handled by navigation */ }
            is ActiveWorkoutEvent.RemoveExercise -> removeExercise(event.workoutExerciseId)
            is ActiveWorkoutEvent.AddSet -> addSet(event.workoutExerciseId, event.weight, event.reps, event.rpe)
            is ActiveWorkoutEvent.UpdateSet -> updateSet(event.setId, event.weight, event.reps, event.rpe, event.workoutExerciseId)
            is ActiveWorkoutEvent.DeleteSet -> deleteSet(event.setId)
            ActiveWorkoutEvent.ShowFinishDialog -> _state.update { it.copy(showFinishDialog = true) }
            ActiveWorkoutEvent.HideFinishDialog -> _state.update { it.copy(showFinishDialog = false) }
            ActiveWorkoutEvent.ShowDiscardDialog -> _state.update { it.copy(showDiscardDialog = true) }
            ActiveWorkoutEvent.HideDiscardDialog -> _state.update { it.copy(showDiscardDialog = false) }
            is ActiveWorkoutEvent.FinishWorkout -> finishWorkout(event.note)
            ActiveWorkoutEvent.DiscardWorkout -> discardWorkout()
        }
    }
    
    private fun loadWorkout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            workoutRepository.getWorkoutByIdFlow(workoutId).collect { workout ->
                if (workout != null) {
                    // Load last performances and progression advice for each exercise
                    val lastPerformanceMap = mutableMapOf<Long, com.example.stracker.domain.model.WorkoutExercise>()
                    val adviceMap = mutableMapOf<Long, com.example.stracker.domain.model.ProgressionAdvice>()
                    
                    workout.exercises.forEach { we ->
                        val exerciseId = we.exercise.id
                        val lastPerformances = workoutRepository.getLastPerformance(exerciseId, 1)
                        lastPerformances.firstOrNull()?.let {
                            lastPerformanceMap[exerciseId] = it
                        }
                        
                        val advice = getProgressionAdviceUseCase(exerciseId, we.exercise.category)
                        adviceMap[exerciseId] = advice
                    }
                    
                    _state.update { state ->
                        state.copy(
                            workout = workout,
                            lastPerformance = lastPerformanceMap,
                            progressionAdvice = adviceMap,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    private fun startTimer() {
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutById(workoutId)
            val startTime = workout?.startedAt?.toEpochMilliseconds() ?: System.currentTimeMillis()
            
            while (true) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _state.update { it.copy(elapsedSeconds = elapsed) }
                delay(1000)
            }
        }
    }
    
    private fun addSet(workoutExerciseId: Long, weight: Float, reps: Int, rpe: Int?) {
        viewModelScope.launch {
            try {
                workoutRepository.addSet(workoutExerciseId, weight, reps, rpe)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun updateSet(setId: Long, weight: Float, reps: Int, rpe: Int?, workoutExerciseId: Long) {
        viewModelScope.launch {
            try {
                val set = ExerciseSet(
                    id = setId,
                    setNumber = 0, // Will be ignored
                    weight = weight,
                    reps = reps,
                    rpe = rpe,
                    isCompleted = true
                )
                workoutRepository.updateSet(set, workoutExerciseId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun deleteSet(setId: Long) {
        viewModelScope.launch {
            try {
                workoutRepository.deleteSet(setId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun removeExercise(workoutExerciseId: Long) {
        viewModelScope.launch {
            try {
                workoutRepository.removeExerciseFromWorkout(workoutExerciseId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun finishWorkout(note: String?) {
        viewModelScope.launch {
            try {
                workoutRepository.finishWorkout(workoutId, note)
                _workoutFinished.emit(Unit)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun discardWorkout() {
        viewModelScope.launch {
            try {
                workoutRepository.discardWorkout(workoutId)
                _workoutFinished.emit(Unit)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
