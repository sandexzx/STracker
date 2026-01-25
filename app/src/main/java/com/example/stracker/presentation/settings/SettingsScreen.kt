package com.example.stracker.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stracker.domain.model.E1RMFormula
import com.example.stracker.domain.model.WeightUnit
import com.example.stracker.presentation.common.components.*
import com.example.stracker.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Настройки",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
        
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Accent)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calculations Section
                SectionTitle("Расчёты")
                
                STrackerCard {
                    SettingsRow(
                        title = "Формула e1RM",
                        subtitle = "Используется для расчёта максимума на 1 повторение"
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            E1RMFormula.entries.forEach { formula ->
                                SettingsChip(
                                    text = formula.displayName,
                                    isSelected = state.settings.e1rmFormula == formula,
                                    onClick = { 
                                        viewModel.onEvent(SettingsEvent.UpdateE1RMFormula(formula))
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Units Section
                SectionTitle("Единицы измерения")
                
                STrackerCard {
                    SettingsRow(
                        title = "Единицы веса",
                        subtitle = "Килограммы или фунты"
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            WeightUnit.entries.forEach { unit ->
                                SettingsChip(
                                    text = unit.displayName,
                                    isSelected = state.settings.weightUnit == unit,
                                    onClick = { 
                                        viewModel.onEvent(SettingsEvent.UpdateWeightUnit(unit))
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Border)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingsRow(
                        title = "Шаг веса по умолчанию",
                        subtitle = "Для быстрых кнопок +/-"
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0.5f, 1.25f, 2.5f, 5f).forEach { step ->
                                SettingsChip(
                                    text = "$step ${state.settings.weightUnit.symbol}",
                                    isSelected = state.settings.defaultWeightStep == step,
                                    onClick = { 
                                        viewModel.onEvent(SettingsEvent.UpdateDefaultWeightStep(step))
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Display Section
                SectionTitle("Отображение")
                
                STrackerCard {
                    SettingsToggleRow(
                        title = "Показывать RPE",
                        subtitle = "Оценка воспринимаемой нагрузки (1-10)",
                        isChecked = state.settings.showRpe,
                        onCheckedChange = { 
                            viewModel.onEvent(SettingsEvent.UpdateShowRpe(it))
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Border)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingsRow(
                        title = "Тема оформления",
                        subtitle = "Внешний вид приложения"
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "system" to "Системная",
                                "light" to "Светлая",
                                "dark" to "Тёмная"
                            ).forEach { (value, label) ->
                                SettingsChip(
                                    text = label,
                                    isSelected = state.settings.theme == value,
                                    onClick = { 
                                        viewModel.onEvent(SettingsEvent.UpdateTheme(value))
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Timer Section
                SectionTitle("Таймер отдыха")
                
                STrackerCard {
                    SettingsToggleRow(
                        title = "Таймер отдыха",
                        subtitle = "Автоматический запуск после подхода",
                        isChecked = state.settings.restTimerEnabled,
                        onCheckedChange = { 
                            viewModel.onEvent(SettingsEvent.UpdateRestTimerEnabled(it))
                        }
                    )
                    
                    if (state.settings.restTimerEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Border)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        SettingsRow(
                            title = "Время отдыха по умолчанию",
                            subtitle = "Секунды между подходами"
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(60, 90, 120, 180).forEach { seconds ->
                                    SettingsChip(
                                        text = "${seconds}с",
                                        isSelected = state.settings.defaultRestSeconds == seconds,
                                        onClick = { 
                                            viewModel.onEvent(SettingsEvent.UpdateDefaultRestSeconds(seconds))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // About Section
                SectionTitle("О приложении")
                
                STrackerCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Версия",
                            color = TextPrimary
                        )
                        Text(
                            text = "1.0.0",
                            color = TextMuted
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        color = TextMuted,
        letterSpacing = 0.2.sp
    )
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Background,
                checkedTrackColor = Accent,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Card
            )
        )
    }
}

@Composable
private fun SettingsChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = if (isSelected) TextPrimary else TextMuted,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Accent.copy(alpha = 0.2f) else Chip)
            .border(
                1.dp,
                if (isSelected) Accent else Border,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}
