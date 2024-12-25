package com.example.sitaa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2196F3),
    secondary = Color(0xFFFF4081),
    background = Color(0xFFF5F5F5)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF2196F3),
    secondary = Color(0xFFFF4081),
    background = Color(0xFF121212)
)

@Composable
fun SitaaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}