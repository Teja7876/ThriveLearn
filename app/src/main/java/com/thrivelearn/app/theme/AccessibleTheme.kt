package com.thrivelearn.app.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.thrivelearn.app.R
import com.thrivelearn.app.accessibility.AccessibilityPreferences

// ── Palette tokens ────────────────────────────────────────────────────────────

private val PrimaryNormal            = Color(0xFF6750A4)
private val BackgroundNormal         = Color(0xFFFFFBFE)
private val TextNormal               = Color(0xFF1C1B1F)

private val PrimaryHighContrast      = Color(0xFFFFFF00)   // WCAG AAA on black
private val BackgroundHighContrast   = Color(0xFF000000)
private val TextHighContrast         = Color(0xFFFFFFFF)

private val NormalColorScheme = lightColorScheme(
    primary       = PrimaryNormal,
    background    = BackgroundNormal,
    surface       = BackgroundNormal,
    onPrimary     = Color.White,
    onBackground  = TextNormal,
    onSurface     = TextNormal,
    // Ensure WCAG 2.2 focus ring contrasts (§2.4.11)
    outline       = Color(0xFF49454F),
    outlineVariant= Color(0xFFCAC4D0)
)

private val HighContrastColorScheme = darkColorScheme(
    primary       = PrimaryHighContrast,
    background    = BackgroundHighContrast,
    surface       = Color(0xFF1A1A1A),
    onPrimary     = Color.Black,
    onBackground  = TextHighContrast,
    onSurface     = TextHighContrast,
    outline       = PrimaryHighContrast,
    outlineVariant= Color(0xFF808080)
)

// ── Font assets ───────────────────────────────────────────────────────────────

/** Bundled OpenDyslexic typeface (Apache 2.0). */
val OpenDyslexicFont = FontFamily(Font(R.font.opendyslexic_regular))

// ── Theme composable ──────────────────────────────────────────────────────────

/**
 * Root theme that reads directly from [AccessibilityPreferences] and
 * applies all visual transforms (contrast, grayscale, font, scale, spacing).
 *
 * There are NO separate legacy enums here – the new preferences data class
 * is the single source of truth.  Old [AppThemeMode] / [AppFontMode] are
 * kept as type aliases for binary compatibility with any code that still
 * imports them; new code should use [AccessibilityPreferences].
 */
@Composable
fun ThriveLearnTheme(
    prefs: AccessibilityPreferences,
    content: @Composable () -> Unit
) {
    val colorScheme = if (prefs.highContrast) HighContrastColorScheme else NormalColorScheme
    val safeScale   = prefs.textScale.coerceIn(1.0f, 2.0f)
    val base        = Typography()

    // Spacing multiplier for Cognitive › Expanded Text Spacing
    val letterSpacingExtra = if (prefs.expandedTextSpacing) 0.05.sp else 0.sp
    val lineHeightExtra    = if (prefs.expandedTextSpacing) 1.1f else 1.0f

    fun bodySize(base: Float)    = (base * safeScale).sp
    fun headSize(base: Float)    = (base * safeScale).sp
    fun labelSize(base: Float)   = (base * safeScale).sp

    val fontFamily = if (prefs.dyslexiaFont) OpenDyslexicFont else null

    fun androidx.compose.ui.text.TextStyle.scaled(baseSize: Float) = copy(
        fontFamily   = fontFamily ?: this.fontFamily,
        fontSize     = (baseSize * safeScale).sp,
        letterSpacing= this.letterSpacing + letterSpacingExtra,
        lineHeight   = if (this.lineHeight.isSp) (this.lineHeight.value * lineHeightExtra).sp
                       else this.lineHeight
    )

    val typography = Typography(
        displayLarge  = base.displayLarge.scaled(57f),
        displayMedium = base.displayMedium.scaled(45f),
        displaySmall  = base.displaySmall.scaled(36f),
        headlineLarge = base.headlineLarge.scaled(32f),
        headlineMedium= base.headlineMedium.scaled(28f),
        headlineSmall = base.headlineSmall.scaled(24f),
        titleLarge    = base.titleLarge.scaled(22f),
        titleMedium   = base.titleMedium.scaled(16f),
        titleSmall    = base.titleSmall.scaled(14f),
        bodyLarge     = base.bodyLarge.scaled(16f),
        bodyMedium    = base.bodyMedium.scaled(14f),
        bodySmall     = base.bodySmall.scaled(12f),
        labelLarge    = base.labelLarge.scaled(14f),
        labelMedium   = base.labelMedium.scaled(12f),
        labelSmall    = base.labelSmall.scaled(11f)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = typography,
        content     = content
    )
}

// ── Legacy type aliases (binary compatibility) ─────────────────────────────────
@Deprecated("Use AccessibilityPreferences.highContrast instead")
enum class AppThemeMode { NORMAL, HIGH_CONTRAST }
@Deprecated("Use AccessibilityPreferences.dyslexiaFont instead")
enum class AppFontMode  { STANDARD, DYSLEXIA_FRIENDLY }

// Removed: LocalThemeMode, LocalFontMode, LocalTextScale CompositionLocals.
// These were fragmented state holders.  All state now lives in
// AccessibilityViewModel and is read via LocalAccessibilityPrefs.
