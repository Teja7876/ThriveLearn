package com.thrivelearn.app.accessibility

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Persistent accessibility preference store backed by SharedPreferences.
 *
 * All I/O runs on [Dispatchers.IO] so the main thread is never blocked.
 * The API mirrors the "AsyncStorage" contract requested: typed getters/setters
 * for every preference key, plus atomic bulk save/load via [AccessibilityPreferences].
 *
 * Keys are private constants so external code can't accidentally mis-type them.
 * All numeric values are clamped to valid ranges before persistence to prevent
 * corrupt state from reaching the UI layer.
 */
class AccessibilityStorageManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Async bulk operations ─────────────────────────────────────────────────

    /**
     * Load the full [AccessibilityPreferences] snapshot from storage.
     * Runs on the IO thread; safe to call from a coroutine or LaunchedEffect.
     */
    suspend fun load(): AccessibilityPreferences = withContext(Dispatchers.IO) {
        AccessibilityPreferences(
            textScale       = prefs.getFloat(KEY_TEXT_SCALE, 1.0f).coerceIn(TEXT_SCALE_MIN, TEXT_SCALE_MAX),
            highContrast    = prefs.getBoolean(KEY_HIGH_CONTRAST, false),
            grayscale       = prefs.getBoolean(KEY_GRAYSCALE, false),
            dyslexiaFont    = prefs.getBoolean(KEY_DYSLEXIA_FONT, false),
            expandedTextSpacing = prefs.getBoolean(KEY_EXPANDED_SPACING, false),
            focusMode       = prefs.getBoolean(KEY_FOCUS_MODE, false),
            ttsEnabled      = prefs.getBoolean(KEY_TTS_ENABLED, false),
            hapticFeedback  = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, false),
            activeProfile   = prefs.getString(KEY_ACTIVE_PROFILE, AccessibilityProfile.NONE.name)
                ?.let { runCatching { AccessibilityProfile.valueOf(it) }.getOrNull() }
                ?: AccessibilityProfile.NONE
        )
    }

    /**
     * Persist the full [AccessibilityPreferences] snapshot atomically.
     * Uses [SharedPreferences.Editor.apply] (async commit) so it never
     * blocks the calling coroutine beyond the IO dispatcher handoff.
     */
    suspend fun save(prefs: AccessibilityPreferences): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit()
            .putFloat(KEY_TEXT_SCALE,         prefs.textScale.coerceIn(TEXT_SCALE_MIN, TEXT_SCALE_MAX))
            .putBoolean(KEY_HIGH_CONTRAST,    prefs.highContrast)
            .putBoolean(KEY_GRAYSCALE,        prefs.grayscale)
            .putBoolean(KEY_DYSLEXIA_FONT,    prefs.dyslexiaFont)
            .putBoolean(KEY_EXPANDED_SPACING, prefs.expandedTextSpacing)
            .putBoolean(KEY_FOCUS_MODE,       prefs.focusMode)
            .putBoolean(KEY_TTS_ENABLED,      prefs.ttsEnabled)
            .putBoolean(KEY_HAPTIC_FEEDBACK,  prefs.hapticFeedback)
            .putString(KEY_ACTIVE_PROFILE,    prefs.activeProfile.name)
            .apply()
    }

    // ── Granular typed setters (used for single-key live updates) ─────────────

    suspend fun setTextScale(value: Float): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit()
            .putFloat(KEY_TEXT_SCALE, value.coerceIn(TEXT_SCALE_MIN, TEXT_SCALE_MAX))
            .apply()
    }

    suspend fun setHighContrast(enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
    }

    suspend fun setGrayscale(enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().putBoolean(KEY_GRAYSCALE, enabled).apply()
    }

    suspend fun setDyslexiaFont(enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().putBoolean(KEY_DYSLEXIA_FONT, enabled).apply()
    }

    suspend fun setExpandedTextSpacing(enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().putBoolean(KEY_EXPANDED_SPACING, enabled).apply()
    }

    suspend fun setFocusMode(enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().putBoolean(KEY_FOCUS_MODE, enabled).apply()
    }

    suspend fun setTtsEnabled(enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().putBoolean(KEY_TTS_ENABLED, enabled).apply()
    }

    suspend fun setHapticFeedback(enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK, enabled).apply()
    }

    suspend fun setActiveProfile(profile: AccessibilityProfile): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().putString(KEY_ACTIVE_PROFILE, profile.name).apply()
    }

    /** Wipe all stored preferences, returning to system defaults. */
    suspend fun reset(): Unit = withContext(Dispatchers.IO) {
        this@AccessibilityStorageManager.prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "accessibility_v2"
        private const val KEY_TEXT_SCALE       = "text_scale"
        private const val KEY_HIGH_CONTRAST    = "high_contrast"
        private const val KEY_GRAYSCALE        = "grayscale"
        private const val KEY_DYSLEXIA_FONT    = "dyslexia_font"
        private const val KEY_EXPANDED_SPACING = "expanded_text_spacing"
        private const val KEY_FOCUS_MODE       = "focus_mode"
        private const val KEY_TTS_ENABLED      = "tts_enabled"
        private const val KEY_HAPTIC_FEEDBACK  = "haptic_feedback"
        private const val KEY_ACTIVE_PROFILE   = "active_profile"

        const val TEXT_SCALE_MIN = 1.0f
        const val TEXT_SCALE_MAX = 2.0f
    }
}
