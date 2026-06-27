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

    // Logic for hardware actions
    LaunchedEffect(triggeredAction) {
        when (triggeredAction) {
            AppAction.DICTATE -> { /* Add your existing logic */ }
            AppAction.SAVE -> { /* Add your existing logic */ }
            else -> {}
        }
        onActionConsumed()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = noteText, 
            onValueChange = { noteText = it }, 
            modifier = Modifier.weight(1f).fillMaxWidth()
        )
        
        // NEW: Smart Summarize Button
        Button(
            onClick = { 
                if (noteText.length > 100) {
                    noteText = TextSummarizer.summarize(noteText)
                    Toast.makeText(context, "Note simplified", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Note too short to summarize", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Auto-Simplify Text")
        }
    }
}
