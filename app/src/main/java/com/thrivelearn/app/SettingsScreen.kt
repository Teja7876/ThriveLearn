package com.thrivelearn.app

import android.view.KeyEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thrivelearn.app.theme.AppFontMode
import com.thrivelearn.app.theme.AppThemeMode
import com.thrivelearn.app.theme.LocalFontMode
import com.thrivelearn.app.theme.LocalThemeMode

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val themeMode = LocalThemeMode.current
    val fontMode = LocalFontMode.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Accessibility Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("High contrast colors")
            Switch(
                checked = themeMode.value == AppThemeMode.HIGH_CONTRAST,
                onCheckedChange = { enabled ->
                    themeMode.value = if (enabled) AppThemeMode.HIGH_CONTRAST else AppThemeMode.NORMAL
                }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Dyslexia-friendly font")
            Switch(
                checked = fontMode.value == AppFontMode.DYSLEXIA_FRIENDLY,
                onCheckedChange = { enabled ->
                    fontMode.value = if (enabled) AppFontMode.DYSLEXIA_FRIENDLY else AppFontMode.STANDARD
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Button Mapping", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Volume Up Action:")
        Button(onClick = { HardwareActionManager.dictateKey = KeyEvent.KEYCODE_VOLUME_UP }) { Text("Set to Dictate") }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Volume Down Action:")
        Button(onClick = { HardwareActionManager.saveKey = KeyEvent.KEYCODE_VOLUME_DOWN }) { Text("Set to Save") }
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Back to Library") }
    }
}
