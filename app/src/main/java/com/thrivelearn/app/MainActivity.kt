package com.thrivelearn.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thrivelearn.app.theme.*
import java.io.OutputStreamWriter

class MainActivity : ComponentActivity() {
    private lateinit var speechEngine: ThriveSpeechEngine
    private lateinit var ttsEngine: ThriveTextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speechEngine = ThriveSpeechEngine(applicationContext)
        ttsEngine = ThriveTextToSpeech(applicationContext)

        setContent {
            val currentTheme = remember { mutableStateOf(AppThemeMode.NORMAL) }

            CompositionLocalProvider(LocalThemeMode provides currentTheme) {
                ThriveLearnTheme(themeMode = currentTheme.value) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreenLayout(speechEngine = speechEngine, ttsEngine = ttsEngine)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        ttsEngine.shutdown()
        super.onDestroy()
    }
}

@Composable
fun MainScreenLayout(speechEngine: ThriveSpeechEngine, ttsEngine: ThriveTextToSpeech) {
    var fontScaleMultiplier by remember { mutableFloatStateOf(1.0f) }
    var currentTab by remember { mutableIntStateOf(0) }
    val currentTheme = LocalThemeMode.current

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(if (currentTab == 0) "Workspace" else "Library", fontSize = (20 * fontScaleMultiplier).sp) },
                actions = {
                    IconButton(
                        onClick = { 
                            currentTheme.value = if (currentTheme.value == AppThemeMode.NORMAL) AppThemeMode.HIGH_CONTRAST else AppThemeMode.NORMAL
                        },
                        modifier = Modifier.semantics { contentDescription = "Toggle high contrast mode. Current is ${currentTheme.value.name}" }
                    ) { Text(if (currentTheme.value == AppThemeMode.NORMAL) "?" else "?", fontSize = 20.sp) }
                    IconButton(
                        onClick = { fontScaleMultiplier = if (fontScaleMultiplier >= 1.8f) 1.0f else fontScaleMultiplier + 0.2f },
                        modifier = Modifier.semantics { contentDescription = "Increase text size. Current scale is ${fontScaleMultiplier}x" }
                    ) { Text("A+", fontSize = 16.sp) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("??") }, label = { Text("Notes") }, selected = currentTab == 0, onClick = { currentTab = 0 },
                    modifier = Modifier.semantics { contentDescription = "Switch to Note Taking Workspace" }
                )
                NavigationBarItem(
                    icon = { Text("??") }, label = { Text("Library") }, selected = currentTab == 1, onClick = { currentTab = 1 },
                    modifier = Modifier.semantics { contentDescription = "Switch to Document and Media Library" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (currentTab == 0) {
                AccessibleNoteScreenContent(speechEngine, ttsEngine, fontScaleMultiplier)
            } else {
                DocumentLibraryScreen(fontScaleMultiplier)
            }
        }
    }
}

@Composable
fun AccessibleNoteScreenContent(speechEngine: ThriveSpeechEngine, ttsEngine: ThriveTextToSpeech, fontScaleMultiplier: Float) {
    val context = LocalContext.current
    var noteText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var isReading by remember { mutableStateOf(false) }
    var liveTranscription by remember { mutableStateOf("") }
    val currentFontSize = (16 * fontScaleMultiplier).sp

    val saveDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer -> writer.write(noteText) }
                }
                Toast.makeText(context, "Note Saved Successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = if (isRecording && liveTranscription.isNotEmpty()) "$noteText $liveTranscription" else noteText,
            onValueChange = { noteText = it },
            label = { Text("Type or dictate notes here", fontSize = currentFontSize) },
            textStyle = LocalTextStyle.current.copy(fontSize = currentFontSize, color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.fillMaxWidth().weight(1f).semantics { contentDescription = "Interactive study notebook pane" },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (isRecording) {
                            speechEngine.stopListening()
                            if (liveTranscription.isNotEmpty()) {
                                noteText = "$noteText $liveTranscription".trim()
                                liveTranscription = ""
                            }
                            isRecording = false
                        } else {
                            ttsEngine.stop()
                            isReading = false
                            speechEngine.startListening(
                                onResult = { text -> liveTranscription = text },
                                onError = { isRecording = false }
                            )
                            isRecording = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f).height(56.dp).semantics {
                        contentDescription = if (isRecording) "Stop dictation" else "Start live speech to text"
                        role = Role.Button
                    }
                ) { Text(if (isRecording) "Stop" else "Dictate", fontSize = currentFontSize) }

                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            saveDocumentLauncher.launch("ThriveNote_${System.currentTimeMillis()}.txt")
                        } else {
                            Toast.makeText(context, "Note box is empty", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.weight(1f).height(56.dp).semantics {
                        contentDescription = "Save this note directly onto your phone storage"
                        role = Role.Button
                    }
                ) { Text("Save Note", fontSize = currentFontSize) }
            }

            // New Large Accessibility Button for Reading Text Aloud
            Button(
                onClick = {
                    if (isReading) {
                        ttsEngine.stop()
                        isReading = false
                    } else if (noteText.isNotBlank()) {
                        ttsEngine.speak(noteText)
                        isReading = true
                    } else {
                        Toast.makeText(context, "No text to read", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp).semantics {
                    contentDescription = if (isReading) "Stop reading text aloud" else "Read entire note aloud"
                    role = Role.Button
                }
            ) { Text(if (isReading) "Stop Reading" else "Read Aloud", fontSize = currentFontSize) }
        }
    }
}
