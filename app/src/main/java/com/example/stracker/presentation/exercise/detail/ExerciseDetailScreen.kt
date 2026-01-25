package com.example.stracker.presentation.exercise.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stracker.domain.model.WorkoutExercise
import com.example.stracker.presentation.common.components.*
import com.example.stracker.ui.theme.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ExerciseDetailScreen(
    exerciseId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ExerciseDetailViewModel = hiltViewModel()
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
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = state.exercise?.name ?: "Упражнение",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            Pill(text = "📈 e1RM")
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // E1RM Chart Card
            item {
                E1RMChartCard(
                    e1rmHistory = state.e1rmHistory,
                    averageE1RM = state.averageE1RM,
                    trendPercent = state.trendPercent
                )
            }
            
            // Filters
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Pill(text = "Epley")
                    Pill(text = "Период 3 мес")
                    Pill(text = "Только рабочие")
                }
            }
            
            // Last Performances Section
            item {
                Text(
                    text = "ПОСЛЕДНИЕ ВЫПОЛНЕНИЯ",
                    fontSize = 12.sp,
                    color = TextMuted,
                    letterSpacing = 0.2.sp
                )
            }
            
            if (state.performances.isEmpty()) {
                item {
                    STrackerCard {
                        Text(
                            text = "Нет данных о выполнении",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(state.performances) { performance ->
                    PerformanceCard(performance = performance)
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun E1RMChartCard(
    e1rmHistory: List<Float>,
    averageE1RM: Float,
    trendPercent: Float
) {
    val latestE1RM = e1rmHistory.lastOrNull() ?: 0f
    
    STrackerCard {
        Text(
            text = "Прогресс e1RM",
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${latestE1RM.toInt()} кг • последние ${e1rmHistory.size} сессий",
            color = TextMuted,
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Chart
        if (e1rmHistory.isNotEmpty()) {
            E1RMChart(
                data = e1rmHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Accent.copy(alpha = 0.15f), Background)
                        )
                    )
                    .border(1.dp, Border, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Недостаточно данных",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LegendItem(
                label = "Среднее",
                value = "${averageE1RM.toInt()} кг",
                modifier = Modifier.weight(1f)
            )
            LegendItem(
                label = "Тренд",
                value = "${if (trendPercent >= 0) "+" else ""}${"%.1f".format(trendPercent)}%",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun E1RMChart(
    data: List<Float>,
    modifier: Modifier = Modifier
) {
    val minValue = data.minOrNull() ?: 0f
    val maxValue = data.maxOrNull() ?: 100f
    val range = (maxValue - minValue).coerceAtLeast(1f)
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Accent.copy(alpha = 0.15f), Background)
                )
            )
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (data.size < 2) return@Canvas
            
            val stepX = size.width / (data.size - 1)
            val points = data.mapIndexed { index, value ->
                val x = index * stepX
                val y = size.height - ((value - minValue) / range * size.height)
                Offset(x, y)
            }
            
            // Draw line
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(listOf(Accent, AccentLight)),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            
            // Draw dots
            points.forEach { point ->
                drawCircle(
                    color = AccentLight,
                    radius = 4.dp.toPx(),
                    center = point
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Card2)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun PerformanceCard(performance: WorkoutExercise) {
    // Get date from workout if available (we'll use a placeholder for now)
    val setsText = performance.sets
        .filter { it.isCompleted && !it.isWarmup }
        .joinToString(", ") { "${it.weight.toInt()}×${it.reps}" }
    
    val e1rm = performance.bestE1RM
    val volume = performance.totalVolume
    
    STrackerCard {
        Text(
            text = "Тренировка",
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        if (setsText.isNotEmpty()) {
            Text(
                text = setsText,
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "e1RM: ${e1rm.toInt()} кг • Объём: ${volume.toInt()} кг",
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}
