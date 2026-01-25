package com.example.stracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.stracker.domain.model.E1RMFormula
import com.example.stracker.domain.model.UserSettings
import com.example.stracker.domain.model.WeightUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val E1RM_FORMULA = stringPreferencesKey("e1rm_formula")
        val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        val DEFAULT_WEIGHT_STEP = floatPreferencesKey("default_weight_step")
        val SHOW_RPE = booleanPreferencesKey("show_rpe")
        val THEME = stringPreferencesKey("theme")
        val REST_TIMER_ENABLED = booleanPreferencesKey("rest_timer_enabled")
        val DEFAULT_REST_SECONDS = intPreferencesKey("default_rest_seconds")
    }
    
    val settings: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            e1rmFormula = preferences[PreferencesKeys.E1RM_FORMULA]?.let { 
                E1RMFormula.valueOf(it) 
            } ?: E1RMFormula.EPLEY,
            weightUnit = preferences[PreferencesKeys.WEIGHT_UNIT]?.let { 
                WeightUnit.valueOf(it) 
            } ?: WeightUnit.KG,
            defaultWeightStep = preferences[PreferencesKeys.DEFAULT_WEIGHT_STEP] ?: 2.5f,
            showRpe = preferences[PreferencesKeys.SHOW_RPE] ?: true,
            theme = preferences[PreferencesKeys.THEME] ?: "system",
            restTimerEnabled = preferences[PreferencesKeys.REST_TIMER_ENABLED] ?: false,
            defaultRestSeconds = preferences[PreferencesKeys.DEFAULT_REST_SECONDS] ?: 90
        )
    }
    
    suspend fun updateE1RMFormula(formula: E1RMFormula) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.E1RM_FORMULA] = formula.name
        }
    }
    
    suspend fun updateWeightUnit(unit: WeightUnit) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEIGHT_UNIT] = unit.name
        }
    }
    
    suspend fun updateDefaultWeightStep(step: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_WEIGHT_STEP] = step
        }
    }
    
    suspend fun updateShowRpe(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_RPE] = show
        }
    }
    
    suspend fun updateTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }
    
    suspend fun updateRestTimerEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REST_TIMER_ENABLED] = enabled
        }
    }
    
    suspend fun updateDefaultRestSeconds(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_REST_SECONDS] = seconds
        }
    }
}
