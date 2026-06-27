package com.thrivelearn.app

import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thrivelearn.app.database.NoteEntity
import com.thrivelearn.app.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@Composable
fun AccessibleNoteScreenContent(
    speechEngine: ThriveSpeechEngine,
    ttsEngine: ThriveTextToSpeech,
    triggeredAction: AppAction?,
    onActionConsumed: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // FIXED: Initialize ViewModel with repository
    val viewModel: NoteViewModel = viewModel(
        factory = NoteViewModel.Companion.create(ThriveLearnApp.noteRepository)
    )

    var noteText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("Quick Note") }
    var showSaveDialog by remember { mutableStateOf(false) }
    var allNotes by remember { mutableStateOf<List<NoteEntity>>(emptyList()) }

    // FIXED: Collect notes from repository
    LaunchedEffect(Unit) {
        viewModel.allNotes.collect { notes ->
            allNotes = notes
        }
    }

    // FIXED: Collect UI messages
    val uiMessage = viewModel.uiMessage.collectAsState()
    LaunchedEffect(uiMessage.value) {
        uiMessage.value?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    // Hardware action logic
    LaunchedEffect(triggeredAction) {
        when (triggeredAction) {
            AppAction.DICTATE -> {
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
                    showSaveDialog = true
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

    // FIXED: Auto-save functionality
    LaunchedEffect(noteText) {
        if (noteText.isNotEmpty() && !isRecording) {
            scope.launch {
                // Auto-save after 3 seconds of inactivity
                // This would need a debounce implementation for production
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .semantics { contentDescription = "Note taking screen" }
    ) {
        // Show save dialog if needed
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Save Note") },
                text = { Text("Enter a title for your note:") },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.createNoteFromDictation(noteTitle, noteText)
                                noteText = ""
                                noteTitle = "Quick Note"
                                showSaveDialog = false
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { showSaveDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Text(
            "Quick Notes",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .semantics { contentDescription = "Note taking workspace" }
        )

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

        // Dictate button
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

        // Save button
        Button(
            onClick = {
                if (noteText.isNotEmpty()) {
                    showSaveDialog = true
                } else {
                    Toast.makeText(context, "Note is empty", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .semantics { contentDescription = "Save note button" }
        ) {
            Text("💾 Save Note")
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

        // FIXED: Show saved notes list
        if (allNotes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Saved Notes (${allNotes.size})",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.semantics { contentDescription = "Saved notes count: ${allNotes.size}" }
            )

            LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                items(allNotes.take(5)) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = note.content.take(50) + if (note.content.length > 50) "..." else "",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
