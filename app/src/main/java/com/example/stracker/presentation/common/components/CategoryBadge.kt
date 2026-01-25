package com.example.stracker.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stracker.domain.model.ExerciseCategory
import com.example.stracker.ui.theme.*

@Composable
fun CategoryBadge(
    category: ExerciseCategory,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (category) {
        ExerciseCategory.COMPOUND -> BadgeCompound
        ExerciseCategory.ACCESSORY -> Accent2.copy(alpha = 0.15f)
        ExerciseCategory.ISOLATION -> Warning.copy(alpha = 0.15f)
    }
    
    val textColor = when (category) {
        ExerciseCategory.COMPOUND -> BadgeCompoundText
        ExerciseCategory.ACCESSORY -> Accent2
        ExerciseCategory.ISOLATION -> Warning
    }
    
    val borderColor = when (category) {
        ExerciseCategory.COMPOUND -> BadgeCompoundBorder
        ExerciseCategory.ACCESSORY -> Accent2.copy(alpha = 0.35f)
        ExerciseCategory.ISOLATION -> Warning.copy(alpha = 0.35f)
    }
    
    Text(
        text = category.displayName,
        fontSize = 11.sp,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
