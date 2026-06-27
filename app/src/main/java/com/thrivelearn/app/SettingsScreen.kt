package com.thrivelearn.app

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thrivelearn.app.theme.AppThemeMode
import com.thrivelearn.app.theme.AppFontMode
import com.thrivelearn.app.theme.LocalThemeMode
import com.thrivelearn.app.theme.LocalFontMode

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val themeMode = LocalThemeMode.current
    val fontMode = LocalFontMode.current
    var dictateKeyName by remember { mutableStateOf("Volume Up") }
    var saveKeyName by remember { mutableStateOf("Volume Down") }
    var readAloudKeyName by remember { mutableStateOf("Power Button") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Settings screen for accessibility options" }
    ) {
        // Header
        Text(
            "Accessibility Settings",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .semantics { contentDescription = "Accessibility Settings header" }
        )

        // FIXED: Theme Toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Display Theme",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { contentDescription = "Display Theme section" }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        themeMode.value = if (themeMode.value == AppThemeMode.NORMAL) {
                            AppThemeMode.HIGH_CONTRAST
                        } else {
                            AppThemeMode.NORMAL
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription =
                                "Toggle high contrast theme. Current: ${themeMode.value.name}"
                        }
                ) {
                    Text(
                        "${themeMode.value.name} Mode",
                        fontSize = 14.sp
                    )
                }
            }
        }

        // FIXED: Font Mode Toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Font Mode",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { contentDescription = "Font Mode section" }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        fontMode.value = if (fontMode.value == AppFontMode.STANDARD) {
                            AppFontMode.DYSLEXIA_FRIENDLY
                        } else {
                            AppFontMode.STANDARD
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription =
                                "Toggle dyslexia-friendly font. Current: ${fontMode.value.name}"
                        }
                ) {
                    Text(
                        "${fontMode.value.name} Font",
                        fontSize = 14.sp
                    )
                }
            }
        }

        // FIXED: Hardware Button Mapping
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Hardware Button Mapping",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { contentDescription = "Hardware Button Mapping section" }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Dictate Key Mapping
                Text(
                    "Dictation Button (Currently: $dictateKeyName)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.semantics { contentDescription = "Dictation button setting" }
                )
                Button(
                    onClick = { HardwareActionManager.remapKey(AppAction.DICTATE, KeyEvent.KEYCODE_VOLUME_UP) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Volume Up")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Save Key Mapping
                Text(
                    "Save Button (Currently: $saveKeyName)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.semantics { contentDescription = "Save button setting" }
                )
                Button(
                    onClick = { HardwareActionManager.remapKey(AppAction.SAVE, KeyEvent.KEYCODE_VOLUME_DOWN) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Volume Down")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Read Aloud Key Mapping
                Text(
                    "Read Aloud Button (Currently: $readAloudKeyName)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.semantics { contentDescription = "Read aloud button setting" }
                )
                Button(
                    onClick = { HardwareActionManager.remapKey(AppAction.READ_ALOUD, KeyEvent.KEYCODE_POWER) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Power Button")
                }
            }
        }

        // Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "ℹ️ Tips",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.semantics { contentDescription = "Tips section" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "• High Contrast mode improves visibility for low vision users\n" +
                            "• Dyslexia-friendly font requires font file to be installed\n" +
                            "• Hardware buttons can be remapped for your convenience\n" +
                            "• Changes are saved automatically",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back Button
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Back to library button" }
        ) {
            Text("Back to Library")
        }
    }
}
