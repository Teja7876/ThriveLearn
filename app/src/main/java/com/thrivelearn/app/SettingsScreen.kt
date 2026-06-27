package com.thrivelearn.app

import android.view.KeyEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
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
