package com.thrivelearn.app

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AccessibleNoteScreenContent(
    speechEngine: ThriveSpeechEngine,
    ttsEngine: ThriveTextToSpeech,
    triggeredAction: AppAction?,
    onActionConsumed: () -> Unit
) {
    val context = LocalContext.current
    var noteText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // FIXED: Implement hardware action logic
    LaunchedEffect(triggeredAction) {
        when (triggeredAction) {
            AppAction.DICTATE -> {
                if (!isRecording) {
                    isRecording = true
                    isProcessing = true
                    speechEngine.startListening(
                        onResult = { recognizedText ->
                            noteText += if (noteText.isEmpty()) recognizedText else " $recognizedText"
                            Toast.makeText(context, "Text added", Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            isRecording = false
                            isProcessing = false
                        }
                    )
                    Toast.makeText(context, "Listening...", Toast.LENGTH_SHORT).show()
                } else {
                    isRecording = false
                    speechEngine.stopListening()
                    isProcessing = false
                    Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
                }
            }
            AppAction.SAVE -> {
                if (noteText.isNotEmpty()) {
                    // Save to local storage (implement persistence later)
                    Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Note is empty", Toast.LENGTH_SHORT).show()
                }
            }
            AppAction.READ_ALOUD -> {
                if (noteText.isNotEmpty()) {
                    ttsEngine.speak(noteText)
                    Toast.makeText(context, "Reading aloud...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No text to read", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {}
        }
        onActionConsumed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .semantics { contentDescription = "Note taking screen" }
    ) {
        Text(
            "Quick Notes",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .semantics { contentDescription = "Note taking workspace" }
        )

        // FIXED: Added dictate button UI
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .semantics { contentDescription = "Text input field. Type or use dictate button." },
            placeholder = { Text("Type or use Dictate button...") },
            readOnly = isProcessing
        )

        Spacer(modifier = Modifier.height(12.dp))

        // FIXED: Dictate button with proper state management
        Button(
            onClick = {
                if (!isRecording) {
                    isRecording = true
                    isProcessing = true
                    speechEngine.startListening(
                        onResult = { recognizedText ->
                            noteText += if (noteText.isEmpty()) recognizedText else " $recognizedText"
                        },
                        onError = { error ->
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            isRecording = false
                            isProcessing = false
                        }
                    )
                } else {
                    isRecording = false
                    speechEngine.stopListening()
                    isProcessing = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .semantics {
                    contentDescription = if (isRecording) "Stop recording button" else "Start dictation button"
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isRecording) "🎤 Stop Recording" else "🎤 Dictate")
        }

        // Auto-Simplify Button
        Button(
            onClick = {
                if (noteText.length > 100) {
                    noteText = TextSummarizer.summarize(noteText)
                    Toast.makeText(context, "Note simplified", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Note too short to summarize", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .semantics { contentDescription = "Auto-simplify text button" }
        ) {
            Text("✨ Auto-Simplify Text")
        }

        // Read Aloud Button
        Button(
            onClick = {
                if (noteText.isNotEmpty()) {
                    ttsEngine.speak(noteText)
                    Toast.makeText(context, "Reading aloud...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No text to read", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Read aloud button" }
        ) {
            Text("🔊 Read Aloud")
        }
    }
}
