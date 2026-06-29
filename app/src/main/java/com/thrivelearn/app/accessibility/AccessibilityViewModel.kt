package com.thrivelearn.app.accessibility

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Single source of truth for all accessibility state.
 * Survives configuration changes (rotation, font-scale change) because it
 * extends [AndroidViewModel].  The UI observes [uiState] as a [StateFlow].
 *
 * Every setter is non-blocking: it updates the in-memory state immediately
 * (so the UI responds without jank), then persists asynchronously.
 */
class AccessibilityViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = AccessibilityStorageManager(application)

    private val _uiState = MutableStateFlow(AccessibilityPreferences())
    val uiState: StateFlow<AccessibilityPreferences> = _uiState.asStateFlow()

    /** True while the initial load from storage is in progress. */
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = storage.load()
            _isLoading.value = false
        }
    }

    // ── Text Controls ─────────────────────────────────────────────────────────

    fun increaseTextScale() = updateAndSave(
        _uiState.value.copy(
            textScale = (_uiState.value.textScale + 0.1f)
                .coerceAtMost(AccessibilityStorageManager.TEXT_SCALE_MAX),
            activeProfile = AccessibilityProfile.NONE
        )
    )

    fun decreaseTextScale() = updateAndSave(
        _uiState.value.copy(
            textScale = (_uiState.value.textScale - 0.1f)
                .coerceAtLeast(AccessibilityStorageManager.TEXT_SCALE_MIN),
            activeProfile = AccessibilityProfile.NONE
        )
    )

    fun setTextScale(value: Float) = updateAndSave(
        _uiState.value.copy(
            textScale = value.coerceIn(
                AccessibilityStorageManager.TEXT_SCALE_MIN,
                AccessibilityStorageManager.TEXT_SCALE_MAX
            ),
            activeProfile = AccessibilityProfile.NONE
        )
    )

    // ── Vision ────────────────────────────────────────────────────────────────

    fun setHighContrast(enabled: Boolean) = updateAndSave(
        _uiState.value.copy(
            highContrast = enabled,
            activeProfile = AccessibilityProfile.NONE
        )
    )

    fun setGrayscale(enabled: Boolean) = updateAndSave(
        _uiState.value.copy(
            grayscale = enabled,
            activeProfile = AccessibilityProfile.NONE
        )
    )

    // ── Cognitive ─────────────────────────────────────────────────────────────

    fun setDyslexiaFont(enabled: Boolean) = updateAndSave(
        _uiState.value.copy(
            dyslexiaFont = enabled,
            activeProfile = AccessibilityProfile.NONE
        )
    )

    fun setExpandedTextSpacing(enabled: Boolean) = updateAndSave(
        _uiState.value.copy(
            expandedTextSpacing = enabled,
            activeProfile = AccessibilityProfile.NONE
        )
    )

    // ── Motor ─────────────────────────────────────────────────────────────────

    fun setFocusMode(enabled: Boolean) = updateAndSave(
        _uiState.value.copy(
            focusMode = enabled,
            activeProfile = AccessibilityProfile.NONE
        )
    )

    // ── Assistive ─────────────────────────────────────────────────────────────

    fun setTtsEnabled(enabled: Boolean) = updateAndSave(
        _uiState.value.copy(
            ttsEnabled = enabled,
            activeProfile = AccessibilityProfile.NONE
        )
    )

    fun setHapticFeedback(enabled: Boolean) = updateAndSave(
        _uiState.value.copy(
            hapticFeedback = enabled,
            activeProfile = AccessibilityProfile.NONE
        )
    )

    // ── Profiles ──────────────────────────────────────────────────────────────

    fun applyProfile(profile: AccessibilityProfile) = updateAndSave(
        profile.toPreferences()
    )

    // ── Reset ─────────────────────────────────────────────────────────────────

    fun resetToDefaults() {
        val defaults = AccessibilityPreferences()
        _uiState.value = defaults
        viewModelScope.launch { storage.reset() }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun updateAndSave(new: AccessibilityPreferences) {
        _uiState.value = new
        viewModelScope.launch { storage.save(new) }
    }
}
