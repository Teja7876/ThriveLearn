package com.thrivelearn.app

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.thrivelearn.app.theme.*

class MainActivity : ComponentActivity() {
    private lateinit var speechEngine: ThriveSpeechEngine
    private lateinit var ttsEngine: ThriveTextToSpeech
    
    // State to trigger actions from hardware
    var triggeredAction by mutableStateOf<AppAction?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speechEngine = ThriveSpeechEngine(applicationContext)
        ttsEngine = ThriveTextToSpeech(applicationContext)

        setContent {
            val currentTheme = remember { mutableStateOf(AppThemeMode.NORMAL) }
            val currentFont = remember { mutableStateOf(AppFontMode.STANDARD) }

            CompositionLocalProvider(
                LocalThemeMode provides currentTheme,
                LocalFontMode provides currentFont
            ) {
                ThriveLearnTheme(themeMode = currentTheme.value, fontMode = currentFont.value) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        MainScreenLayout(speechEngine, ttsEngine, triggeredAction) { triggeredAction = null }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val action = HardwareActionManager.getActionForKey(keyCode)
        if (action != null) {
            triggeredAction = action
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        speechEngine.stopListening()
        ttsEngine.shutdown()
        super.onDestroy()
    }
}

@Composable
fun MainScreenLayout(speechEngine: ThriveSpeechEngine, ttsEngine: ThriveTextToSpeech, triggeredAction: AppAction?, onActionConsumed: () -> Unit) {
    var currentTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Notes", "Materials")
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        label = { Text(label) },
                        icon = { Text(if (index == 0) "Aa" else "File") },
                        modifier = Modifier.semantics {
                            contentDescription = "Open $label"
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (currentTab == 0) {
                AccessibleNoteScreenContent(speechEngine, ttsEngine, triggeredAction, onActionConsumed)
            } else {
                DocumentLibraryScreen(ttsEngine = ttsEngine)
            }
        }
    }
}
