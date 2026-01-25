package com.example.stracker.presentation.workout.active

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stracker.domain.model.ExerciseSet
import com.example.stracker.domain.model.ProgressionAdvice
import com.example.stracker.domain.model.ProgressionTrend
import com.example.stracker.domain.model.WorkoutExercise
import com.example.stracker.presentation.common.components.*
import com.example.stracker.ui.theme.*

@Composable
fun ActiveWorkoutScreen(
    workoutId: Long,
    onNavigateBack: () -> Unit,
    onAddExercise: () -> Unit,
    onWorkoutFinished: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.workoutFinished.collect {
            onWorkoutFinished()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopBar(
                elapsedSeconds = state.elapsedSeconds,
                onBack = { viewModel.onEvent(ActiveWorkoutEvent.ShowDiscardDialog) }
            )
            
            // Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                state.workout?.exercises?.let { exercises ->
                    items(exercises, key = { it.id }) { workoutExercise ->
                        ExerciseCard(
                            workoutExercise = workoutExercise,
                            lastPerformance = state.lastPerformance[workoutExercise.exercise.id],
                            advice = state.progressionAdvice[workoutExercise.exercise.id],
                            onAddSet = { weight, reps, rpe ->
                                viewModel.onEvent(
                                    ActiveWorkoutEvent.AddSet(workoutExercise.id, weight, reps, rpe)
                                )
                            },
                            onDeleteSet = { setId ->
                                viewModel.onEvent(ActiveWorkoutEvent.DeleteSet(setId))
                            },
                            onRemoveExercise = {
                                viewModel.onEvent(ActiveWorkoutEvent.RemoveExercise(workoutExercise.id))
                            }
                        )
                    }
                }
                
                item {
                    DashedButton(
                        text = "➕ Добавить упражнение",
                        onClick = onAddExercise
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SecondaryButton(
                        text = "🏁 Завершить тренировку",
                        onClick = { viewModel.onEvent(ActiveWorkoutEvent.ShowFinishDialog) }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
        
        // Dialogs
        if (state.showFinishDialog) {
            FinishWorkoutDialog(
                onDismiss = { viewModel.onEvent(ActiveWorkoutEvent.HideFinishDialog) },
                onConfirm = { note ->
                    viewModel.onEvent(ActiveWorkoutEvent.FinishWorkout(note))
                }
            )
        }
        
        if (state.showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(ActiveWorkoutEvent.HideDiscardDialog) },
                title = { Text("Отменить тренировку?") },
                text = { Text("Все данные этой тренировки будут потеряны.") },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.onEvent(ActiveWorkoutEvent.DiscardWorkout) }
                    ) {
                        Text("Отменить", color = Danger)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onEvent(ActiveWorkoutEvent.HideDiscardDialog) }
                    ) {
                        Text("Продолжить")
                    }
                },
                containerColor = Panel
            )
        }
    }
}

@Composable
private fun TopBar(
    elapsedSeconds: Long,
    onBack: () -> Unit
) {
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)
    
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
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Тренировка",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        
        Pill(text = "⏱️ $timeString")
    }
}

@Composable
private fun ExerciseCard(
    workoutExercise: WorkoutExercise,
    lastPerformance: WorkoutExercise?,
    advice: ProgressionAdvice?,
    onAddSet: (Float, Int, Int?) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onRemoveExercise: () -> Unit
) {
    var showAddSetDialog by remember { mutableStateOf(false) }
    
    STrackerCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workoutExercise.exercise.name,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                lastPerformance?.let { lp ->
                    val setsText = lp.sets.filter { it.isCompleted && !it.isWarmup }
                        .joinToString(", ") { "${it.weight.toInt()}×${it.reps}" }
                    if (setsText.isNotEmpty()) {
                        Text(
                            text = "Прошлый раз: $setsText",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryBadge(category = workoutExercise.exercise.category)
                IconButton(
                    onClick = onRemoveExercise,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        // Advice
        advice?.let { adv ->
            if (adv.trend != ProgressionTrend.FIRST_TIME || workoutExercise.sets.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AdviceRow(advice = adv)
            }
        }
        
        // Sets Table
        if (workoutExercise.sets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            SetsTable(
                sets = workoutExercise.sets,
                onDeleteSet = onDeleteSet
            )
        }
        
        // Add Set Button
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Border, RoundedCornerShape(12.dp))
                .clickable { showAddSetDialog = true }
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+ Добавить подход",
                color = TextMuted,
                fontSize = 14.sp
            )
        }
    }
    
    if (showAddSetDialog) {
        AddSetDialog(
            lastSet = workoutExercise.sets.lastOrNull(),
            advice = advice,
            onDismiss = { showAddSetDialog = false },
            onConfirm = { weight, reps, rpe ->
                onAddSet(weight, reps, rpe)
                showAddSetDialog = false
            }
        )
    }
}

@Composable
private fun AdviceRow(advice: ProgressionAdvice) {
    val backgroundColor = when (advice.trend) {
        ProgressionTrend.PROGRESSING -> Accent2.copy(alpha = 0.12f)
        ProgressionTrend.PLATEAU -> Warning.copy(alpha = 0.12f)
        ProgressionTrend.REGRESSING -> Danger.copy(alpha = 0.12f)
        ProgressionTrend.FIRST_TIME -> Accent.copy(alpha = 0.12f)
    }
    
    val textColor = when (advice.trend) {
        ProgressionTrend.PROGRESSING -> Color(0xFFC8F7E5)
        ProgressionTrend.PLATEAU -> Color(0xFFFFE9C4)
        ProgressionTrend.REGRESSING -> Color(0xFFFFD1D1)
        ProgressionTrend.FIRST_TIME -> AccentLight
    }
    
    val icon = when (advice.trend) {
        ProgressionTrend.PROGRESSING -> "📈"
        ProgressionTrend.PLATEAU -> "⏸️"
        ProgressionTrend.REGRESSING -> "⚠️"
        ProgressionTrend.FIRST_TIME -> "💡"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 14.sp)
        Text(
            text = advice.message,
            color = textColor,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SetsTable(
    sets: List<ExerciseSet>,
    onDeleteSet: (Long) -> Unit
) {
    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("#", color = TextMuted, fontSize = 12.sp, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
            Text("Вес", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Повт", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("RPE", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(40.dp))
        }
        
        HorizontalDivider(color = Border)
        
        // Rows
        sets.forEachIndexed { index, set ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${index + 1}",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.width(30.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${set.weight}",
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
                IconButton(
                    onClick = { onDeleteSet(set.id) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            if (index < sets.lastIndex) {
                HorizontalDivider(color = Border)
            }
        }
    }
}

@Composable
private fun AddSetDialog(
    lastSet: ExerciseSet?,
    advice: ProgressionAdvice?,
    onDismiss: () -> Unit,
    onConfirm: (Float, Int, Int?) -> Unit
) {
    var weight by remember { 
        mutableStateOf(
            (advice?.recommendedWeight ?: lastSet?.weight ?: 0f).toString()
        ) 
    }
    var reps by remember { 
        mutableStateOf(
            (advice?.recommendedReps ?: lastSet?.reps ?: 8).toString()
        ) 
    }
    var rpe by remember { mutableStateOf(lastSet?.rpe?.toString() ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить подход") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Вес (кг)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Повторения") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rpe,
                    onValueChange = { rpe = it },
                    label = { Text("RPE (1-10, опционально)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val w = weight.toFloatOrNull() ?: 0f
                    val r = reps.toIntOrNull() ?: 0
                    val rpeValue = rpe.toIntOrNull()?.coerceIn(1, 10)
                    if (w > 0 && r > 0) {
                        onConfirm(w, r, rpeValue)
                    }
                }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        containerColor = Panel
    )
}

@Composable
private fun FinishWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (String?) -> Unit
) {
    var note by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Завершить тренировку") },
        text = {
            Column {
                Text("Добавьте заметку (опционально):")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("Заметка...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(note.ifBlank { null }) }
            ) {
                Text("Завершить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        containerColor = Panel
    )
}
