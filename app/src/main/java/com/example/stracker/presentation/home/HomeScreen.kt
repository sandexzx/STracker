package com.example.stracker.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stracker.presentation.common.components.*
import com.example.stracker.ui.theme.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HomeScreen(
    onStartWorkout: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onContinueWorkout: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.navigateToWorkout.collect { workoutId ->
            onStartWorkout(workoutId)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STracker",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Pill(text = "⚙️ Настройки")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Main CTA Button
            if (state.activeWorkout != null) {
                // Кнопка продолжения активной тренировки
                PrimaryButton(
                    text = "▶️ ПРОДОЛЖИТЬ ТРЕНИРОВКУ",
                    onClick = { viewModel.onEvent(HomeEvent.ContinueWorkout) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DashedButton(
                    text = "🏋️ Начать новую",
                    onClick = { viewModel.onEvent(HomeEvent.StartWorkout) }
                )
            } else {
                PrimaryButton(
                    text = "🏋️ НАЧАТЬ ТРЕНИРОВКУ",
                    onClick = { viewModel.onEvent(HomeEvent.StartWorkout) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Last Workout Section
            SectionTitle("Последняя тренировка")
            Spacer(modifier = Modifier.height(8.dp))
            
            state.lastWorkout?.let { workout ->
                LastWorkoutCard(workout)
            } ?: run {
                STrackerCard {
                    Text(
                        text = "Нет завершённых тренировок",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick Stats Section
            SectionTitle("Быстрая статистика")
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    value = state.totalWorkouts.toString(),
                    label = "тренировок",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = state.totalExercises.toString(),
                    label = "упражнений",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = state.activeDays.toString(),
                    label = "активных дней",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tip Section
            SectionTitle("Совет дня")
            Spacer(modifier = Modifier.height(8.dp))
            
            STrackerCard {
                Text(
                    text = "Плавная прогрессия",
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Если RPE ≥ 9, удерживаем вес без прибавки.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            
            // Bottom padding for nav bar
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // Bottom Navigation
        BottomNavBar(
            currentRoute = "home",
            onNavigate = { item ->
                when (item) {
                    BottomNavItem.HOME -> { /* Already here */ }
                    BottomNavItem.HISTORY -> onNavigateToHistory()
                    BottomNavItem.EXERCISES -> onNavigateToExercises()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
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
private fun LastWorkoutCard(workout: com.example.stracker.domain.model.Workout) {
    val dateTime = workout.startedAt.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateText = "${dateTime.dayOfMonth} ${getMonthName(dateTime.monthNumber)}"
    
    STrackerCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = dateText,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${workout.totalExercises} упражнений • ${workout.durationMinutes} мин",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Pill(text = "Завершена")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        val progress = if (workout.totalExercises > 0) 1f else 0f
        STrackerProgressBar(progress = progress)
    }
}

private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "янв"
        2 -> "фев"
        3 -> "мар"
        4 -> "апр"
        5 -> "мая"
        6 -> "июн"
        7 -> "июл"
        8 -> "авг"
        9 -> "сен"
        10 -> "окт"
        11 -> "ноя"
        12 -> "дек"
        else -> ""
    }
}
