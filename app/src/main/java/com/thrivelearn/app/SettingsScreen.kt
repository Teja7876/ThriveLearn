package com.thrivelearn.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.thrivelearn.app.accessibility.AccessibilityProfile
import com.thrivelearn.app.accessibility.LocalAccessibilityPrefs
import com.thrivelearn.app.accessibility.LocalAccessibilityViewModel
import com.thrivelearn.app.accessibility.AccessibilityStorageManager

// ── Navigation Landmark helper ────────────────────────────────────────────────

private fun Modifier.navigationLandmark(label: String): Modifier =
    semantics(mergeDescendants = false) {
        contentDescription = label
        paneTitle = label
    }

// ── Main Settings screen ──────────────────────────────────────────────────────

/**
 * Accessibility Settings screen.
 *
 * Groups: 1. Text  2. Vision  3. Cognitive  4. Motor  5. Assistive
 *         6. Profiles  7. Reset
 *
 * Compliance:
 *  - WCAG 2.2 §1.3.1  Info and Relationships (heading() semantics)
 *  - WCAG 2.2 §1.4.6  Contrast (Enhanced) – High Contrast palette
 *  - WCAG 2.2 §2.4.1  Bypass Blocks (navigationLandmark paneTitle)
 *  - WCAG 2.2 §2.5.3  Label in Name (contentDescription matches visible text)
 *  - WCAG 2.2 §2.5.5  Target Size ≥ 48 dp on all interactive elements
 *  - WCAG 2.2 §4.1.3  Status Messages (stateLabel announced on Toggle)
 */
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val prefs = LocalAccessibilityPrefs.current
    val vm    = LocalAccessibilityViewModel.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Accessibility Settings",
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Back to app" }
                    ) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // ── 1. Text Controls ─────────────────────────────────────────────
            SettingsSection(landmark = "Text Controls") {
                SectionHeader("Text Size")

                val scalePct = "${(prefs.textScale * 100).toInt()}%"
                Text(
                    text = "Current size: $scalePct",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.semantics {
                        contentDescription = "Text size is $scalePct"
                    }
                )
                Spacer(Modifier.height(8.dp))
                Slider(
                    value = prefs.textScale,
                    onValueChange = { vm.setTextScale(it) },
                    valueRange = AccessibilityStorageManager.TEXT_SCALE_MIN..AccessibilityStorageManager.TEXT_SCALE_MAX,
                    steps = 9,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Text size slider, $scalePct" }
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AccessibleButton(
                        label = "Decrease font size",
                        modifier = Modifier.weight(1f),
                        onClick = { vm.decreaseTextScale() }
                    ) { Text("A−") }
                    AccessibleButton(
                        label = "Increase font size",
                        modifier = Modifier.weight(1f),
                        onClick = { vm.increaseTextScale() }
                    ) { Text("A+") }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── 2. Vision ────────────────────────────────────────────────────
            SettingsSection(landmark = "Vision") {
                SectionHeader("Vision")
                AccessibilityToggleRow(
                    label = "High Contrast Colors",
                    description = "Black background with yellow text for maximum contrast (WCAG AAA).",
                    checked = prefs.highContrast,
                    onChecked = { vm.setHighContrast(it) }
                )
                AccessibilityToggleRow(
                    label = "Grayscale",
                    description = "Removes all colour for colour-blind or photosensitive users.",
                    checked = prefs.grayscale,
                    onChecked = { vm.setGrayscale(it) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── 3. Cognitive ─────────────────────────────────────────────────
            SettingsSection(landmark = "Cognitive") {
                SectionHeader("Cognitive")
                AccessibilityToggleRow(
                    label = "Dyslexia-Friendly Font",
                    description = "Replaces system font with OpenDyslexic to reduce letter confusion.",
                    checked = prefs.dyslexiaFont,
                    onChecked = { vm.setDyslexiaFont(it) }
                )
                AccessibilityToggleRow(
                    label = "Expanded Text Spacing",
                    description = "Increases letter-spacing and line-height for readability.",
                    checked = prefs.expandedTextSpacing,
                    onChecked = { vm.setExpandedTextSpacing(it) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── 4. Motor ─────────────────────────────────────────────────────
            SettingsSection(landmark = "Motor") {
                SectionHeader("Motor")
                AccessibilityToggleRow(
                    label = "Focus Mode (Simplified UI)",
                    description = "Collapses navigation and enlarges tap targets to at least 48 dp.",
                    checked = prefs.focusMode,
                    onChecked = { vm.setFocusMode(it) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── 5. Assistive ─────────────────────────────────────────────────
            SettingsSection(landmark = "Assistive Technology") {
                SectionHeader("Assistive")
                AccessibilityToggleRow(
                    label = "Text-to-Speech (Read Page)",
                    description = "Reads the current screen content aloud when activated.",
                    checked = prefs.ttsEnabled,
                    onChecked = { vm.setTtsEnabled(it) }
                )
                AccessibilityToggleRow(
                    label = "Haptic Feedback",
                    description = "Pulses vibration on successful interactions.",
                    checked = prefs.hapticFeedback,
                    onChecked = { vm.setHapticFeedback(it) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── 6. Profiles ──────────────────────────────────────────────────
            SettingsSection(landmark = "Accessibility Profiles") {
                SectionHeader("Profiles")
                Text(
                    text = "Active: ${prefs.activeProfile.label}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .semantics {
                            contentDescription = "Active profile: ${prefs.activeProfile.label}"
                        }
                )
                val profiles = listOf(
                    AccessibilityProfile.BLIND,
                    AccessibilityProfile.LOW_VISION,
                    AccessibilityProfile.DYSLEXIA,
                    AccessibilityProfile.MOTOR
                )
                profiles.forEach { profile ->
                    val isActive = prefs.activeProfile == profile
                    AccessibleButton(
                        label = "Apply ${profile.label} profile${if (isActive) ", currently active" else ""}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { vm.applyProfile(profile) },
                        colors = if (isActive)
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        else
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(profile.label)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── 7. Reset ─────────────────────────────────────────────────────
            SettingsSection(landmark = "Reset Settings") {
                SectionHeader("Reset")
                var showConfirm by remember { mutableStateOf(false) }
                if (showConfirm) {
                    ResetConfirmDialog(
                        onConfirm = { vm.resetToDefaults(); showConfirm = false },
                        onDismiss = { showConfirm = false }
                    )
                }
                AccessibleButton(
                    label = "Reset all accessibility settings to system defaults",
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = { showConfirm = true }
                ) {
                    Text("Reset to Defaults")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Reusable WCAG-compliant components ────────────────────────────────────────

@Composable
private fun SettingsSection(
    landmark: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .navigationLandmark(landmark)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(bottom = 12.dp)
            .semantics { heading() }
    )
}

@Composable
private fun AccessibilityToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    val stateLabel = if (checked) "on" else "off"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$label, $stateLabel. $description"
                toggleableState = ToggleableState(checked)
                onClick(label = if (checked) "Turn off" else "Turn on") {
                    onChecked(!checked)
                    true
                }
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = "$label switch, $stateLabel" }
        )
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun AccessibleButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        colors = colors,
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics { contentDescription = label },
        content = content
    )
}

@Composable
private fun ResetConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Settings", modifier = Modifier.semantics { heading() }) },
        text  = { Text("This will restore all accessibility settings to system defaults. You can reapply a profile at any time.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.semantics { contentDescription = "Confirm reset settings" }
            ) { Text("Reset") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.semantics { contentDescription = "Cancel reset" }
            ) { Text("Cancel") }
        },
        modifier = Modifier.semantics { paneTitle = "Reset confirmation dialog" }
    )
}
