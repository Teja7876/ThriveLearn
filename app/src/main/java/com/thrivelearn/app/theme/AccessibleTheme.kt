package com.thrivelearn.app.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// Standard Colors
private val PrimaryNormal = Color(0xFF6750A4)
private val BackgroundNormal = Color(0xFFFFFBFE)
private val TextNormal = Color(0xFF1C1B1F)

// High Contrast Colors (WCAG AAA)
private val PrimaryHighContrast = Color(0xFFFFFF00) // Maximum visibility yellow
private val BackgroundHighContrast = Color(0xFF000000) // Pure black
private val TextHighContrast = Color(0xFFFFFFFF) // Pure white

private val NormalColorScheme = lightColorScheme(
    primary = PrimaryNormal,
    background = BackgroundNormal,
    surface = BackgroundNormal,
    onPrimary = Color.White,
    onBackground = TextNormal,
    onSurface = TextNormal
)

private val HighContrastColorScheme = darkColorScheme(
    primary = PrimaryHighContrast,
    background = BackgroundHighContrast,
    surface = BackgroundHighContrast,
    onPrimary = Color.Black,
    onBackground = TextHighContrast,
    onSurface = TextHighContrast
)

// System to track the current mode globally
enum class AppThemeMode {
    NORMAL, HIGH_CONTRAST
}

val LocalThemeMode = compositionLocalOf<MutableState<AppThemeMode>> { 
    error("Theme state missing") 
}

@Composable
fun ThriveLearnTheme(
    themeMode: AppThemeMode,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.HIGH_CONTRAST -> HighContrastColorScheme
        AppThemeMode.NORMAL -> NormalColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}