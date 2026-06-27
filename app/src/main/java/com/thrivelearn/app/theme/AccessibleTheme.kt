package com.thrivelearn.app.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.thrivelearn.app.R

// Standard Colors
private val PrimaryNormal = Color(0xFF6750A4)
private val BackgroundNormal = Color(0xFFFFFBFE)
private val TextNormal = Color(0xFF1C1B1F)

// High Contrast Colors (WCAG AAA)
private val PrimaryHighContrast = Color(0xFFFFFF00)
private val BackgroundHighContrast = Color(0xFF000000)
private val TextHighContrast = Color(0xFFFFFFFF)

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

// FIXED: Made font optional to handle missing resource gracefully
private fun getOpenDyslexicFont(): FontFamily? {
    return try {
        FontFamily(Font(R.font.opendyslexic_regular))
    } catch (e: Exception) {
        // If font not found, return null and use default
        null
    }
}

val OpenDyslexicFont: FontFamily? = getOpenDyslexicFont()

// Global States
enum class AppThemeMode { NORMAL, HIGH_CONTRAST }
enum class AppFontMode { STANDARD, DYSLEXIA_FRIENDLY }

val LocalThemeMode = compositionLocalOf<MutableState<AppThemeMode>> { error("Theme missing") }
val LocalFontMode = compositionLocalOf<MutableState<AppFontMode>> { error("Font missing") }

@Composable
fun ThriveLearnTheme(
    themeMode: AppThemeMode,
    fontMode: AppFontMode,
    content: @Composable () -> Unit
) {
    val colorScheme = if (themeMode == AppThemeMode.HIGH_CONTRAST) HighContrastColorScheme else NormalColorScheme

    // Override material typography if Dyslexia mode is active
    val typography = if (fontMode == AppFontMode.DYSLEXIA_FRIENDLY && OpenDyslexicFont != null) {
        Typography(
            bodyLarge = Typography().bodyLarge.copy(fontFamily = OpenDyslexicFont),
            bodyMedium = Typography().bodyMedium.copy(fontFamily = OpenDyslexicFont),
            bodySmall = Typography().bodySmall.copy(fontFamily = OpenDyslexicFont),
            titleLarge = Typography().titleLarge.copy(fontFamily = OpenDyslexicFont),
            titleMedium = Typography().titleMedium.copy(fontFamily = OpenDyslexicFont),
            titleSmall = Typography().titleSmall.copy(fontFamily = OpenDyslexicFont),
            labelLarge = Typography().labelLarge.copy(fontFamily = OpenDyslexicFont),
            labelMedium = Typography().labelMedium.copy(fontFamily = OpenDyslexicFont),
            labelSmall = Typography().labelSmall.copy(fontFamily = OpenDyslexicFont),
            headlineSmall = Typography().headlineSmall.copy(fontFamily = OpenDyslexicFont),
            headlineMedium = Typography().headlineMedium.copy(fontFamily = OpenDyslexicFont),
            headlineLarge = Typography().headlineLarge.copy(fontFamily = OpenDyslexicFont),
            displaySmall = Typography().displaySmall.copy(fontFamily = OpenDyslexicFont),
            displayMedium = Typography().displayMedium.copy(fontFamily = OpenDyslexicFont),
            displayLarge = Typography().displayLarge.copy(fontFamily = OpenDyslexicFont)
        )
    } else {
        Typography()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
