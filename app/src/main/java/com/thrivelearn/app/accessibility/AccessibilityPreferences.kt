package com.thrivelearn.app.accessibility

/**
 * Immutable snapshot of all accessibility preferences.
 * Serialised to / from SharedPreferences by AccessibilityStorageManager.
 * Groups mirror the Settings UI sections (WCAG 2.2 §1.4, §2.1, §2.4).
 */
data class AccessibilityPreferences(

    // ── 1. Text Controls ─────────────────────────────────────────────────────
    /** Scale factor applied to every text style (1.0 = system default, max 2.0). */
    val textScale: Float = 1.0f,

    // ── 2. Vision ────────────────────────────────────────────────────────────
    /** WCAG 1.4.6 – full high-contrast black/yellow palette. */
    val highContrast: Boolean = false,
    /** Desaturate the entire UI for colour-blind or photosensitive users. */
    val grayscale: Boolean = false,

    // ── 3. Cognitive ─────────────────────────────────────────────────────────
    /** Replace system font with OpenDyslexic. */
    val dyslexiaFont: Boolean = false,
    /** Extra letter-spacing + line-height for readability. */
    val expandedTextSpacing: Boolean = false,

    // ── 4. Motor ─────────────────────────────────────────────────────────────
    /** Collapses secondary navigation; enlarges tap targets (WCAG 2.5.5 ≥ 44 dp). */
    val focusMode: Boolean = false,

    // ── 5. Assistive ─────────────────────────────────────────────────────────
    /** TTS reads the current screen content aloud on demand. */
    val ttsEnabled: Boolean = false,
    /** Optional haptic pulse on every successful interaction. */
    val hapticFeedback: Boolean = false,

    // ── 6. Active profile ────────────────────────────────────────────────────
    /** Which preset bundle is currently active, if any. */
    val activeProfile: AccessibilityProfile = AccessibilityProfile.NONE
)

/** Preset bundles – each applies a specific combination of preferences. */
enum class AccessibilityProfile(val label: String) {
    NONE("Custom"),
    BLIND("Blind"),
    LOW_VISION("Low Vision"),
    DYSLEXIA("Dyslexia"),
    MOTOR("Motor Access")
}

/** Factory functions that build the preset AccessibilityPreferences. */
fun AccessibilityProfile.toPreferences(): AccessibilityPreferences = when (this) {
    AccessibilityProfile.NONE -> AccessibilityPreferences()
    AccessibilityProfile.BLIND -> AccessibilityPreferences(
        textScale = 1.5f,
        highContrast = true,
        ttsEnabled = true,
        hapticFeedback = true,
        activeProfile = this
    )
    AccessibilityProfile.LOW_VISION -> AccessibilityPreferences(
        textScale = 1.6f,
        highContrast = true,
        expandedTextSpacing = true,
        activeProfile = this
    )
    AccessibilityProfile.DYSLEXIA -> AccessibilityPreferences(
        textScale = 1.25f,
        dyslexiaFont = true,
        expandedTextSpacing = true,
        activeProfile = this
    )
    AccessibilityProfile.MOTOR -> AccessibilityPreferences(
        textScale = 1.35f,
        focusMode = true,
        hapticFeedback = true,
        activeProfile = this
    )
}
