package com.example.stracker.presentation.workout.detail

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stracker.domain.model.ExerciseSet
import com.example.stracker.domain.model.WorkoutExercise
import com.example.stracker.presentation.common.components.*
import com.example.stracker.ui.theme.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun WorkoutDetailScreen(
    workoutId: Long,
    onNavigateBack: () -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
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
                    text = "Детали тренировки",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
        
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Accent)
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Ошибка",
                        color = Danger,
                        fontSize = 14.sp
                    )
                }
            }
            state.workout != null -> {
                val workout = state.workout!!
                val dateTime = workout.startedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Workout Summary Card
                    item {
                        STrackerCard {
                            Text(
                                text = "${dateTime.dayOfMonth} ${getMonthName(dateTime.monthNumber)} ${dateTime.year}",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                StatColumn(
                                    value = workout.totalExercises.toString(),
                                    label = "Упражнений"
                                )
                                StatColumn(
                                    value = workout.totalSets.toString(),
                                    label = "Подходов"
                                )
                                StatColumn(
                                    value = "${workout.durationMinutes} мин",
                                    label = "Длительность"
                                )
                            }
                            
                            workout.note?.let { note ->
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Border)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Заметка: $note",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    
                    // Section Title
                    item {
                        Text(
                            text = "УПРАЖНЕНИЯ",
                            fontSize = 12.sp,
                            color = TextMuted,
                            letterSpacing = 0.2.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    
                    // Exercise Cards
                    items(workout.exercises, key = { it.id }) { workoutExercise ->
                        ExerciseDetailCard(workoutExercise = workoutExercise)
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = Accent,
            fontSize = 20.sp
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun ExerciseDetailCard(
    workoutExercise: WorkoutExercise
) {
    val workingSets = workoutExercise.sets.filter { it.isCompleted && !it.isWarmup }
    val warmupSets = workoutExercise.sets.filter { it.isCompleted && it.isWarmup }
    
    STrackerCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = workoutExercise.exercise.name,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            CategoryBadge(category = workoutExercise.exercise.category)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Sets Table Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("#", color = TextMuted, fontSize = 11.sp, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
            Text("Тип", color = TextMuted, fontSize = 11.sp, modifier = Modifier.width(50.dp), textAlign = TextAlign.Center)
            Text("Вес", color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Повт", color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("RPE", color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = Border)
        
        // Warmup Sets
        warmupSets.forEachIndexed { index, set ->
            SetRow(
                setNumber = index + 1,
                set = set,
                isWarmup = true
            )
        }
        
        // Working Sets
        workingSets.forEachIndexed { index, set ->
            SetRow(
                setNumber = warmupSets.size + index + 1,
                set = set,
                isWarmup = false
            )
        }
    }
}

@Composable
private fun SetRow(
    setNumber: Int,
    set: ExerciseSet,
    isWarmup: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$setNumber",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = if (isWarmup) "Разм" else "Раб",
            color = if (isWarmup) Warning else Accent,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "${set.weight} кг",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "${set.reps}",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = set.rpe?.toString() ?: "-",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "января"
        2 -> "февраля"
        3 -> "марта"
        4 -> "апреля"
        5 -> "мая"
        6 -> "июня"
        7 -> "июля"
        8 -> "августа"
        9 -> "сентября"
        10 -> "октября"
        11 -> "ноября"
        12 -> "декабря"
        else -> ""
    }
}
