package com.thrivelearn.app.accessibility

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocals that expose the accessibility ViewModel and current
 * preferences snapshot throughout the Compose tree.
 *
 * Usage:
 *   val prefs = LocalAccessibilityPrefs.current          // read state
 *   val vm    = LocalAccessibilityViewModel.current      // trigger updates
 *
 * Both are provided by [AccessibilityProvider] which is installed once in
 * MainActivity, above the ThriveLearnTheme call.
 */

/** The live preferences snapshot – changes re-compose only affected subtrees. */
val LocalAccessibilityPrefs = compositionLocalOf { AccessibilityPreferences() }

/**
 * Reference to the [AccessibilityViewModel] – stable across recompositions
 * so it uses [staticCompositionLocalOf] (no lambda overhead on every read).
 */
val LocalAccessibilityViewModel = staticCompositionLocalOf<AccessibilityViewModel> {
    error("LocalAccessibilityViewModel not provided. Wrap your root content with AccessibilityProvider.")
}
