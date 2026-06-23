package com.gagandeep.dynamicuistudio.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF006A6A),
    onPrimary = Color.White,
    secondary = Color(0xFF6D5E00),
    tertiary = Color(0xFF8F4C38),
    background = Color(0xFFFBFCFA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8EDEA)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4FD8D5),
    secondary = Color(0xFFD9C75D),
    tertiary = Color(0xFFFFB59E),
    background = Color(0xFF101413),
    surface = Color(0xFF171D1C),
    surfaceVariant = Color(0xFF3F4947)
)

@Composable
fun DynamicUiStudioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
