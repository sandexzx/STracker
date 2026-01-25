package com.example.stracker.presentation.exercise.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import com.example.stracker.domain.model.Exercise
import com.example.stracker.domain.model.MuscleGroup
import com.example.stracker.presentation.common.components.*
import com.example.stracker.ui.theme.*

@Composable
fun ExerciseLibraryScreen(
    onNavigateBack: () -> Unit,
    onExerciseClick: (Long) -> Unit,
    onCreateExercise: () -> Unit,
    viewModel: ExerciseLibraryViewModel = hiltViewModel()
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
                        text = "Упражнения",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                IconButton(onClick = onCreateExercise) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = TextPrimary
                    )
                }
            }
            
            // Search
            var searchText by remember { mutableStateOf("") }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    TextField(
                        value = searchText,
                        onValueChange = { 
                            searchText = it
                            viewModel.onSearchQueryChange(it)
                        },
                        placeholder = { Text("Поиск упражнения", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Accent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Muscle Group Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    text = "Все",
                    isSelected = state.selectedMuscleGroup == null,
                    onClick = { viewModel.onMuscleGroupSelect(null) }
                )
                
                listOf(
                    MuscleGroup.CHEST to "Грудь",
                    MuscleGroup.BACK to "Спина",
                    MuscleGroup.QUADRICEPS to "Ноги",
                    MuscleGroup.SHOULDERS to "Плечи",
                    MuscleGroup.BICEPS to "Руки"
                ).forEach { (group, name) ->
                    FilterChip(
                        text = name,
                        isSelected = state.selectedMuscleGroup == group,
                        onClick = { viewModel.onMuscleGroupSelect(group) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Exercise List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.compoundExercises.isNotEmpty()) {
                    item {
                        SectionTitle("Базовые")
                    }
                    
                    items(state.compoundExercises, key = { it.id }) { exercise ->
                        ExerciseItem(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) }
                        )
                    }
                }
                
                if (state.accessoryExercises.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Вспомогательные")
                    }
                    
                    items(state.accessoryExercises, key = { it.id }) { exercise ->
                        ExerciseItem(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) }
                        )
                    }
                }
                
                if (state.isolationExercises.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Изолирующие")
                    }
                    
                    items(state.isolationExercises, key = { it.id }) { exercise ->
                        ExerciseItem(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) }
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
        
        // Bottom Navigation
        BottomNavBar(
            currentRoute = "exercises",
            onNavigate = { item ->
                when (item) {
                    BottomNavItem.HOME -> onNavigateBack()
                    BottomNavItem.HISTORY -> { /* Navigate */ }
                    BottomNavItem.EXERCISES -> { /* Already here */ }
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
        letterSpacing = 0.2.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = if (isSelected) TextPrimary else TextMuted,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (isSelected) Accent.copy(alpha = 0.2f) else Chip)
            .border(
                1.dp,
                if (isSelected) Accent else Border,
                RoundedCornerShape(999.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
private fun ExerciseItem(
    exercise: Exercise,
    onClick: () -> Unit
) {
    STrackerCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = exercise.primaryMuscle.displayName,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            CategoryBadge(category = exercise.category)
        }
    }
}
