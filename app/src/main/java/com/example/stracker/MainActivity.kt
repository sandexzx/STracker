package com.example.stracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.stracker.presentation.navigation.STrackerNavHost
import com.example.stracker.ui.theme.Background
import com.example.stracker.ui.theme.STrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Включаем edge-to-edge для современного вида (контент заходит под статус-бар)
        enableEdgeToEdge()
        
        setContent {
            STrackerTheme {
                val navController = rememberNavController()
                
                // Используем Scaffold на верхнем уровне для корректной обработки WindowInsets.
                // Это "Senior" подход, обеспечивающий безопасные отступы для всего контента
                // и правильный цвет фона под системными барами.
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Background,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    STrackerNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}