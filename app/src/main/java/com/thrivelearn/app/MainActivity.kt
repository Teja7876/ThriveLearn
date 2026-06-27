package com.thrivelearn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var audioEngine: ThriveAudioEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioEngine = ThriveAudioEngine(applicationContext)

        setContent {
            MaterialTheme {
                var isRecording by remember { mutableStateOf(false) }
                val transcribedText by remember { mutableStateOf("") }
                val outputFile = File(externalCacheDir, "study_note_recording.mp4")

                AccessibleNoteScreen(
                    isRecording = isRecording,
                    transcribedText = transcribedText,
                    onRecordToggle = {
                        if (isRecording) {
                            audioEngine.stopVoiceRecording()
                            isRecording = false
                        } else {
                            audioEngine.startVoiceRecording(outputFile)
                            isRecording = true
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibleNoteScreen(
    isRecording: Boolean,
    transcribedText: String,
    onRecordToggle: () -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    var fontScaleMultiplier by remember { mutableFloatStateOf(1.0f) }
    val currentFontSize = (16 * fontScaleMultiplier).sp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ThriveLearn Workspace", fontSize = (20 * fontScaleMultiplier).sp) },
                actions = {
                    IconButton(
                        onClick = { 
                            fontScaleMultiplier = if (fontScaleMultiplier >= 1.8f) 1.0f else fontScaleMultiplier + 0.2f 
                        },
                        modifier = Modifier.clearAndSetSemantics { 
                            contentDescription = "Increase text size. Current scale is ${fontScaleMultiplier}x"
                            role = Role.Button
                        }
                    ) {
                        Text("A+", fontSize = 16.sp)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Type or view notes here", fontSize = currentFontSize) },
                textStyle = LocalTextStyle.current.copy(fontSize = currentFontSize),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .semantics { contentDescription = "Interactive study notebook pane" },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (transcribedText.isNotEmpty()) {
                        Text(
                            text = "Latest Transcription: $transcribedText",
                            fontSize = currentFontSize,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Button(
                        onClick = onRecordToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics {
                                contentDescription = if (isRecording) "Stop recording" else "Start high quality recording"
                                role = Role.Button
                                liveRegion = LiveRegionMode.Polite
                            }
                    ) {
                        Text(if (isRecording) "Stop Recording" else "Start Accessible Recording", fontSize = currentFontSize)
                    }
                }
            }
        }
    }
}