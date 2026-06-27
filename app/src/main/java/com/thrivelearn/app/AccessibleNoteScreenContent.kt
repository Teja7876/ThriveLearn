package com.thrivelearn.app

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.util.Date

@Composable
fun AccessibleNoteScreenContent(speechEngine: ThriveSpeechEngine, ttsEngine: ThriveTextToSpeech, triggeredAction: AppAction?, onActionConsumed: () -> Unit) {
    val context = LocalContext.current
    val savedNotes = remember { context.getSharedPreferences("study_notes", 0) }
    var noteText by remember { mutableStateOf(savedNotes.getString("latest_note", "") ?: "") }
    var isRecording by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Ready") }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startDictation(
                speechEngine = speechEngine,
                onRecordingChange = { isRecording = it },
                onStatusChange = { statusMessage = it },
                onTextRecognized = { recognized ->
                    noteText = appendRecognizedText(noteText, recognized)
                }
            )
        } else {
            statusMessage = "Microphone permission is needed for dictation."
        }
    }

    fun saveNote() {
        savedNotes.edit()
            .putString("latest_note", noteText)
            .putLong("latest_saved_at", System.currentTimeMillis())
            .apply()
        statusMessage = "Saved ${DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())}"
        Toast.makeText(context, "Study note saved", Toast.LENGTH_SHORT).show()
    }

    fun requestDictation() {
        if (isRecording) {
            speechEngine.stopListening()
            isRecording = false
            statusMessage = "Dictation stopped"
            return
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startDictation(
                speechEngine = speechEngine,
                onRecordingChange = { isRecording = it },
                onStatusChange = { statusMessage = it },
                onTextRecognized = { recognized ->
                    noteText = appendRecognizedText(noteText, recognized)
                }
            )
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(triggeredAction) {
        when (triggeredAction) {
            AppAction.DICTATE -> requestDictation()
            AppAction.SAVE -> saveNote()
            AppAction.READ_ALOUD -> ttsEngine.speak(noteText.ifBlank { "There is no note to read yet." })
            else -> {}
        }
        if (triggeredAction != null) onActionConsumed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Study Workspace", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Write, dictate, simplify, and listen to study notes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        OutlinedTextField(
            value = noteText, 
            onValueChange = { noteText = it }, 
            label = { Text("Study note") },
            placeholder = { Text("Type here or use Dictate to speak your note.") },
            minLines = 10,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 260.dp)
                .semantics { contentDescription = "Study note editor" }
        )

        Text(
            text = statusMessage,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { requestDictation() }, modifier = Modifier.weight(1f)) {
                Text(if (isRecording) "Stop" else "Dictate")
            }
            Button(onClick = { ttsEngine.speak(noteText.ifBlank { "There is no note to read yet." }) }, modifier = Modifier.weight(1f)) {
                Text("Read Aloud")
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { saveNote() }, modifier = Modifier.weight(1f)) {
                Text("Save")
            }
            OutlinedButton(onClick = { ttsEngine.stop() }, modifier = Modifier.weight(1f)) {
                Text("Stop Audio")
            }
        }

        Button(
            onClick = { 
                if (noteText.trim().length > 100) {
                    noteText = TextSummarizer.summarize(noteText)
                    statusMessage = "Simplified into key points"
                    Toast.makeText(context, "Note simplified", Toast.LENGTH_SHORT).show()
                } else {
                    statusMessage = "Add a longer note before simplifying."
                    Toast.makeText(context, "Note too short to summarize", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Auto-Simplify Text")
        }
    }
}

private fun startDictation(
    speechEngine: ThriveSpeechEngine,
    onRecordingChange: (Boolean) -> Unit,
    onStatusChange: (String) -> Unit,
    onTextRecognized: (String) -> Unit
) {
    onRecordingChange(true)
    onStatusChange("Listening...")
    speechEngine.startListening(
        onResult = { recognized ->
            onTextRecognized(recognized)
            onStatusChange("Added dictated text")
            onRecordingChange(false)
            speechEngine.stopListening()
        },
        onError = { message ->
            onStatusChange(message)
            onRecordingChange(false)
            speechEngine.stopListening()
        }
    )
}

private fun appendRecognizedText(currentText: String, recognizedText: String): String {
    val cleaned = recognizedText.trim()
    if (cleaned.isBlank()) return currentText
    return if (currentText.isBlank()) cleaned else "$currentText $cleaned"
}
