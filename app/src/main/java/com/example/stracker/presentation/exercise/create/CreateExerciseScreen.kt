package com.example.stracker.presentation.exercise.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stracker.domain.model.ExerciseCategory
import com.example.stracker.domain.model.MuscleGroup
import com.example.stracker.presentation.common.components.*
import com.example.stracker.ui.theme.*

@Composable
fun CreateExerciseScreen(
    onNavigateBack: () -> Unit,
    onExerciseCreated: () -> Unit,
    viewModel: CreateExerciseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.exerciseCreated.collect {
            onExerciseCreated()
        }
    }
    
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
                    text = if (state.isEditing) "Редактировать упражнение" else "Новое упражнение",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name Input
            STrackerCard {
                Text(
                    text = "Название",
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.onEvent(CreateExerciseEvent.UpdateName(it)) },
                    placeholder = { Text("Например: Жим лёжа", color = TextMuted) },
                    isError = state.nameError != null,
                    supportingText = state.nameError?.let { { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        cursorColor = Accent
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Category Selection
            STrackerCard {
                Text(
                    text = "Категория",
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExerciseCategory.entries.forEach { category ->
                        CategoryChip(
                            text = category.displayName,
                            isSelected = state.category == category,
                            onClick = { viewModel.onEvent(CreateExerciseEvent.UpdateCategory(category)) }
                        )
                    }
                }
            }
            
            // Primary Muscle Selection
            STrackerCard {
                Text(
                    text = "Основная мышечная группа",
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MuscleGroup.entries.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { muscle ->
                                MuscleChip(
                                    text = muscle.displayName,
                                    isSelected = state.primaryMuscle == muscle,
                                    onClick = { viewModel.onEvent(CreateExerciseEvent.UpdatePrimaryMuscle(muscle)) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill remaining space if row is incomplete
                            repeat(3 - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            // Secondary Muscles Selection
            STrackerCard {
                Text(
                    text = "Вторичные мышцы (опционально)",
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MuscleGroup.entries.filter { it != state.primaryMuscle }.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { muscle ->
                                MuscleChip(
                                    text = muscle.displayName,
                                    isSelected = muscle in state.secondaryMuscles,
                                    onClick = { viewModel.onEvent(CreateExerciseEvent.ToggleSecondaryMuscle(muscle)) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(3 - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            // Notes Input
            STrackerCard {
                Text(
                    text = "Заметки (опционально)",
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = { viewModel.onEvent(CreateExerciseEvent.UpdateNotes(it)) },
                    placeholder = { Text("Техника выполнения, советы...", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        cursorColor = Accent
                    ),
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Error Message
            state.error?.let { error ->
                Text(
                    text = error,
                    color = Danger,
                    fontSize = 12.sp
                )
            }
            
            // Save Button
            PrimaryButton(
                text = if (state.isLoading) "Сохранение..." else "💾 Сохранить упражнение",
                onClick = { viewModel.onEvent(CreateExerciseEvent.SaveExercise) },
                enabled = !state.isLoading
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = if (isSelected) TextPrimary else TextMuted,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Accent.copy(alpha = 0.2f) else Chip)
            .border(
                1.dp,
                if (isSelected) Accent else Border,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

@Composable
private fun MuscleChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Accent.copy(alpha = 0.2f) else Chip)
            .border(
                1.dp,
                if (isSelected) Accent else Border,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = if (isSelected) TextPrimary else TextMuted,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
