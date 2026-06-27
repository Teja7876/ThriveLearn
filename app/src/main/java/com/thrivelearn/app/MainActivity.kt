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
import com.thrivelearn.app.theme.*
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var audioEngine: ThriveAudioEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioEngine = ThriveAudioEngine(applicationContext)

        setContent {
            val currentTheme = remember { mutableStateOf(AppThemeMode.NORMAL) }

            CompositionLocalProvider(LocalThemeMode provides currentTheme) {
                ThriveLearnTheme(themeMode = currentTheme.value) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreenLayout(audioEngine = audioEngine)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenLayout(audioEngine: ThriveAudioEngine) {
    // Shared State
    var fontScaleMultiplier by remember { mutableFloatStateOf(1.0f) }
    var currentTab by remember { mutableIntStateOf(0) } // 0 = Notes, 1 = Library
    val currentTheme = LocalThemeMode.current

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Text(
                        text = if (currentTab == 0) "Workspace" else "Library", 
                        fontSize = (20 * fontScaleMultiplier).sp
                    ) 
                },
                actions = {
                    IconButton(
                        onClick = { 
                            currentTheme.value = if (currentTheme.value == AppThemeMode.NORMAL) 
                                AppThemeMode.HIGH_CONTRAST else AppThemeMode.NORMAL
                        },
                        modifier = Modifier.semantics { 
                            contentDescription = "Toggle high contrast mode. Current is ${currentTheme.value.name}"
                        }
                    ) {
                        Text(if (currentTheme.value == AppThemeMode.NORMAL) "☀" else "☾", fontSize = 20.sp)
                    }
                    IconButton(
                        onClick = { 
                            fontScaleMultiplier = if (fontScaleMultiplier >= 1.8f) 1.0f else fontScaleMultiplier + 0.2f 
                        },
                        modifier = Modifier.semantics { 
                            contentDescription = "Increase text size. Current scale is ${fontScaleMultiplier}x"
                        }
                    ) {
                        Text("A+", fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("📝") },
                    label = { Text("Notes") },
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    modifier = Modifier.semantics { contentDescription = "Switch to Note Taking Workspace" }
                )
                NavigationBarItem(
                    icon = { Text("📚") },
                    label = { Text("Library") },
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    modifier = Modifier.semantics { contentDescription = "Switch to Document and Media Library" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (currentTab == 0) {
                // We hoist the recording state here so it survives tab switching
                var isRecording by remember { mutableStateOf(false) }
                val transcribedText by remember { mutableStateOf("") }
                val context = androidx.compose.ui.platform.LocalContext.current
                val outputFile = remember { File(context.externalCacheDir, "study_note_recording.mp4") }

                AccessibleNoteScreenContent(
                    isRecording = isRecording,
                    transcribedText = transcribedText,
                    fontScaleMultiplier = fontScaleMultiplier,
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
            } else {
                DocumentLibraryScreen(fontScaleMultiplier = fontScaleMultiplier)
            }
        }
    }
}

@Composable
fun AccessibleNoteScreenContent(
    isRecording: Boolean,
    transcribedText: String,
    fontScaleMultiplier: Float,
    onRecordToggle: () -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    val currentFontSize = (16 * fontScaleMultiplier).sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Type or view notes here", fontSize = currentFontSize) },
            textStyle = LocalTextStyle.current.copy(
                fontSize = currentFontSize,
                color = MaterialTheme.colorScheme.onBackground
            ),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = onRecordToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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