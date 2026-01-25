package com.example.stracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val STrackerColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Background,
    primaryContainer = Card,
    onPrimaryContainer = TextPrimary,
    secondary = Accent2,
    onSecondary = Background,
    secondaryContainer = Card2,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentLight,
    onTertiary = Background,
    background = Background,
    onBackground = TextPrimary,
    surface = Panel,
    onSurface = TextPrimary,
    surfaceVariant = Card,
    onSurfaceVariant = TextMuted,
    outline = Border,
    outlineVariant = Chip,
    error = Danger,
    onError = Color.White
)

@Composable
fun STrackerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = STrackerColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
