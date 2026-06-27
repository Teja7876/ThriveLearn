package com.thrivelearn.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var selectedTheme by remember { mutableStateOf(AppThemeMode.NORMAL) }
    var selectedFont by remember { mutableStateOf(AppFontMode.STANDARD) }

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
                        listOf(
                            AppThemeMode.NORMAL to "Standard",
                            AppThemeMode.HIGH_CONTRAST to "High Contrast (WCAG AAA)"
                        ).forEach { (theme, label) ->
                            Button(
                                onClick = { selectedTheme = theme },
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
                            AppFontMode.STANDARD to "Standard",
                            AppFontMode.DYSLEXIA_FRIENDLY to "Dyslexia Friendly"
                        ).forEach { (font, label) ->
                            Button(
                                onClick = { selectedFont = font },
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

                    InfoRow(
                        label = "App Name",
                        value = "ThriveLearn"
                    )

                    InfoRow(
                        label = "Version",
                        value = "1.0.0"
                    )

                    InfoRow(
                        label = "Purpose",
                        value = "Accessible learning platform for PwD"
                    )
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
