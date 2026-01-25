package com.example.stracker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stracker.data.local.SettingsDataStore
import com.example.stracker.domain.model.E1RMFormula
import com.example.stracker.domain.model.UserSettings
import com.example.stracker.domain.model.WeightUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val settings: UserSettings = UserSettings(),
    val isLoading: Boolean = true
)

sealed class SettingsEvent {
    data class UpdateE1RMFormula(val formula: E1RMFormula) : SettingsEvent()
    data class UpdateWeightUnit(val unit: WeightUnit) : SettingsEvent()
    data class UpdateDefaultWeightStep(val step: Float) : SettingsEvent()
    data class UpdateShowRpe(val show: Boolean) : SettingsEvent()
    data class UpdateTheme(val theme: String) : SettingsEvent()
    data class UpdateRestTimerEnabled(val enabled: Boolean) : SettingsEvent()
    data class UpdateDefaultRestSeconds(val seconds: Int) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsDataStore.settings.collect { settings ->
                _state.update { it.copy(settings = settings, isLoading = false) }
            }
        }
    }
    
    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsEvent.UpdateE1RMFormula -> {
                    settingsDataStore.updateE1RMFormula(event.formula)
                }
                is SettingsEvent.UpdateWeightUnit -> {
                    settingsDataStore.updateWeightUnit(event.unit)
                }
                is SettingsEvent.UpdateDefaultWeightStep -> {
                    settingsDataStore.updateDefaultWeightStep(event.step)
                }
                is SettingsEvent.UpdateShowRpe -> {
                    settingsDataStore.updateShowRpe(event.show)
                }
                is SettingsEvent.UpdateTheme -> {
                    settingsDataStore.updateTheme(event.theme)
                }
                is SettingsEvent.UpdateRestTimerEnabled -> {
                    settingsDataStore.updateRestTimerEnabled(event.enabled)
                }
                is SettingsEvent.UpdateDefaultRestSeconds -> {
                    settingsDataStore.updateDefaultRestSeconds(event.seconds)
                }
            }
        }
    }
}
