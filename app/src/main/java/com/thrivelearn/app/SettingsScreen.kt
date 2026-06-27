package com.thrivelearn.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thrivelearn.app.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsPreferences = ThriveLearnApp.settingsPreferences

    // FIXED: Load saved settings
    val themeMode = settingsPreferences.themeMode.collectAsState("NORMAL")
    val fontMode = settingsPreferences.fontMode.collectAsState("STANDARD")
    val speechRate = settingsPreferences.speechRate.collectAsState(0.9f)
    val textSize = settingsPreferences.textSize.collectAsState(1.0f)
    val autoSaveEnabled = settingsPreferences.autoSaveEnabled.collectAsState(true)

    var selectedTheme by remember { mutableStateOf(themeMode.value) }
    var selectedFont by remember { mutableStateOf(fontMode.value) }
    var currentSpeechRate by remember { mutableStateOf(speechRate.value) }
    var currentTextSize by remember { mutableStateOf(textSize.value) }
    var autoSave by remember { mutableStateOf(autoSaveEnabled.value) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .semantics { contentDescription = "Settings screen" },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "⚙️ Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { contentDescription = "Settings title" }
            )
        }

        // FIXED: Accessibility Options Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Accessibility options" }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "♿ Accessibility",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .semantics { contentDescription = "Accessibility section" }
                    )

                    // Theme Selection
                    Text(
                        text = "Display Theme",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .semantics { contentDescription = "Theme selection label" }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("NORMAL" to "Standard", "HIGH_CONTRAST" to "High Contrast").forEach { (theme, label) ->
                            Button(
                                onClick = {
                                    selectedTheme = theme
                                    scope.launch {
                                        settingsPreferences.saveThemeMode(theme)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics { contentDescription = label },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedTheme == theme)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(label, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Font Selection
                    Text(
                        text = "Font Style",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .semantics { contentDescription = "Font selection label" }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "STANDARD" to "Standard",
                            "DYSLEXIA_FRIENDLY" to "Dyslexia Friendly"
                        ).forEach { (font, label) ->
                            Button(
                                onClick = {
                                    selectedFont = font
                                    scope.launch {
                                        settingsPreferences.saveFontMode(font)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics { contentDescription = label },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedFont == font)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(label, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // FIXED: Voice & Audio Settings
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Voice and audio settings" }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎤 Voice & Audio",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .semantics { contentDescription = "Voice and audio section" }
                    )

                    // Speech Rate
                    Text(
                        text = "Speech Rate: ${"%,.1f".format(currentSpeechRate)}x",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Slider(
                        value = currentSpeechRate,
                        onValueChange = { newRate ->
                            currentSpeechRate = newRate
                            scope.launch {
                                settingsPreferences.saveSpeechRate(newRate)
                            }
                        },
                        valueRange = 0.5f..2.0f,
                        steps = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Speech rate slider" }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Text Size
                    Text(
                        text = "Text Size: ${"%,.1f".format(currentTextSize)}x",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Slider(
                        value = currentTextSize,
                        onValueChange = { newSize ->
                            currentTextSize = newSize
                            scope.launch {
                                settingsPreferences.saveTextSize(newSize)
                            }
                        },
                        valueRange = 0.8f..2.0f,
                        steps = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Text size slider" }
                    )
                }
            }
        }

        // FIXED: Auto-Save Settings
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Auto-save settings" }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💾 Auto-Save",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .semantics { contentDescription = "Auto-save section" }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Auto-save toggle" },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Auto-save notes",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = autoSave,
                            onCheckedChange = { newValue ->
                                autoSave = newValue
                                scope.launch {
                                    settingsPreferences.setAutoSave(newValue)
                                }
                            }
                        )
                    }
                }
            }
        }

        // FIXED: Hardware Button Configuration
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Hardware button configuration" }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⌨️ Hardware Buttons",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .semantics { contentDescription = "Hardware buttons section" }
                    )

                    HardwareButtonInfo(
                        action = "Dictate",
                        defaultKey = "Volume Up",
                        description = "Press to start/stop voice recording"
                    )

                    HardwareButtonInfo(
                        action = "Save",
                        defaultKey = "Volume Down",
                        description = "Press to save current note"
                    )

                    HardwareButtonInfo(
                        action = "Read Aloud",
                        defaultKey = "Power Button",
                        description = "Press to read current note aloud"
                    )
                }
            }
        }

        // FIXED: App Information
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "App information" }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ℹ️ About",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .semantics { contentDescription = "About section" }
                    )

                    InfoRow(label = "App Name", value = "ThriveLearn")
                    InfoRow(label = "Version", value = "1.0.0")
                    InfoRow(label = "Purpose", value = "Accessible learning for PwD")
                }
            }
        }
    }
}

@Composable
fun HardwareButtonInfo(
    action: String,
    defaultKey: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .semantics { contentDescription = "$action button configuration" }
    ) {
        Text(
            text = action,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = defaultKey,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
