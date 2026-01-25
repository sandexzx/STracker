package com.example.stracker.presentation.workout.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stracker.domain.model.Workout
import com.example.stracker.presentation.common.components.*
import com.example.stracker.ui.theme.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun WorkoutHistoryScreen(
    onNavigateBack: () -> Unit,
    onWorkoutClick: (Long) -> Unit,
    onNavigateToExercises: () -> Unit,
    viewModel: WorkoutHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "История",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Pill(
                    text = "🔽 ${state.filter.displayName}",
                    onClick = { viewModel.onEvent(WorkoutHistoryEvent.ShowFilterDialog) }
                )
            }
            
            // Content
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Accent)
                }
            } else if (state.workouts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет завершённых тренировок",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.thisWeekWorkouts.isNotEmpty()) {
                        item {
                            SectionTitle("На этой неделе")
                        }
                        
                        items(state.thisWeekWorkouts, key = { it.id }) { workout ->
                            WorkoutHistoryItem(
                                workout = workout,
                                onClick = { onWorkoutClick(workout.id) }
                            )
                        }
                    }
                    
                    if (state.earlierWorkouts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SectionTitle("Ранее")
                        }
                        
                        items(state.earlierWorkouts, key = { it.id }) { workout ->
                            WorkoutHistoryItem(
                                workout = workout,
                                onClick = { onWorkoutClick(workout.id) }
                            )
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
        
        // Bottom Navigation
        BottomNavBar(
            currentRoute = "workout/history",
            onNavigate = { item ->
                when (item) {
                    BottomNavItem.HOME -> onNavigateBack()
                    BottomNavItem.HISTORY -> { /* Already here */ }
                    BottomNavItem.EXERCISES -> onNavigateToExercises()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
        
        // Filter Dialog
        if (state.showFilterDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(WorkoutHistoryEvent.HideFilterDialog) },
                title = { Text("Фильтр по периоду") },
                text = {
                    Column {
                        HistoryFilter.entries.forEach { filter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onEvent(WorkoutHistoryEvent.SetFilter(filter)) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.filter == filter,
                                    onClick = { viewModel.onEvent(WorkoutHistoryEvent.SetFilter(filter)) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Accent)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = filter.displayName,
                                    color = if (state.filter == filter) TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.onEvent(WorkoutHistoryEvent.HideFilterDialog) }) {
                        Text("Закрыть")
                    }
                },
                containerColor = Panel
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        color = TextMuted,
        letterSpacing = 0.2.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun WorkoutHistoryItem(
    workout: Workout,
    onClick: () -> Unit
) {
    val dateTime = workout.startedAt.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateText = "${dateTime.dayOfMonth} ${getMonthName(dateTime.monthNumber)}"
    
    STrackerCard(onClick = onClick) {
        Text(
            text = dateText,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${workout.totalExercises} упражнений • ${workout.durationMinutes} мин • ${workout.totalSets} подходов",
            color = TextMuted,
            fontSize = 12.sp
        )
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
