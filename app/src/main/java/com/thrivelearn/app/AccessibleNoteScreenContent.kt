package com.thrivelearn.app

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AccessibleNoteScreenContent(speechEngine: ThriveSpeechEngine, ttsEngine: ThriveTextToSpeech, triggeredAction: AppAction?, onActionConsumed: () -> Unit) {
    val context = LocalContext.current
    var noteText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }

    // React to Hardware Actions
    LaunchedEffect(triggeredAction) {
        when (triggeredAction) {
            AppAction.DICTATE -> {
                isRecording = !isRecording
                if (isRecording) speechEngine.startListening({ noteText += " $it" }, { isRecording = false })
                else speechEngine.stopListening()
                HapticManager.playVibration(context, 100)
            }
            AppAction.SAVE -> {
                // Simplified save for demo; link your existing save logic here
                Toast.makeText(context, "Saved via hardware button", Toast.LENGTH_SHORT).show()
                HapticManager.playVibration(context, 200)
            }
            else -> {}
        }
        onActionConsumed()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = noteText, onValueChange = { noteText = it }, modifier = Modifier.weight(1f).fillMaxWidth())
        Text("Volume Up: Dictate | Volume Down: Save")
    }
}
