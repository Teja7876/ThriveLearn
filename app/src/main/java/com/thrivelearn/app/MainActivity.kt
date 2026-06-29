package com.thrivelearn.app

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.thrivelearn.app.accessibility.AccessibilityViewModel
import com.thrivelearn.app.accessibility.LocalAccessibilityPrefs
import com.thrivelearn.app.accessibility.LocalAccessibilityViewModel
import com.thrivelearn.app.theme.ThriveLearnTheme

class MainActivity : ComponentActivity() {

    // ── Single ViewModel per process (survives rotation) ─────────────────────
    private val accessibilityViewModel: AccessibilityViewModel by viewModels()

    private lateinit var speechEngine: ThriveSpeechEngine
    private lateinit var ttsEngine: ThriveTextToSpeech

    var triggeredAction by androidx.compose.runtime.mutableStateOf<AppAction?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speechEngine = ThriveSpeechEngine(applicationContext)
        ttsEngine    = ThriveTextToSpeech(applicationContext)

        setContent {
            // Observe the live prefs snapshot from the ViewModel
            val prefs by accessibilityViewModel.uiState.collectAsState()
            val isLoading by accessibilityViewModel.isLoading.collectAsState()

            // Provide both the snapshot and the ViewModel to the entire tree
            CompositionLocalProvider(
                LocalAccessibilityViewModel provides accessibilityViewModel,
                LocalAccessibilityPrefs     provides prefs
            ) {
                ThriveLearnTheme(prefs = prefs) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color    = MaterialTheme.colorScheme.background
                    ) {
                        if (!isLoading) {
                            MainScreenLayout(
                                speechEngine    = speechEngine,
                                ttsEngine       = ttsEngine,
                                triggeredAction = triggeredAction,
                                onActionConsumed = { triggeredAction = null }
                            )
                        }
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

// ── Main navigation scaffold ──────────────────────────────────────────────────

@Composable
fun MainScreenLayout(
    speechEngine: ThriveSpeechEngine,
    ttsEngine: ThriveTextToSpeech,
    triggeredAction: AppAction?,
    onActionConsumed: () -> Unit
) {
    val prefs = LocalAccessibilityPrefs.current

    // In Focus Mode, hide the Settings tab from the bottom bar to simplify UI
    var currentTab    by remember { mutableIntStateOf(0) }
    var showSettings  by remember { mutableStateOf(false) }

    val tabs = if (prefs.focusMode)
        listOf("Notes", "Materials")
    else
        listOf("Notes", "Materials", "Tools")

    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick  = { currentTab = index },
                        label    = { Text(label) },
                        icon     = {
                            Text(
                                when (index) {
                                    0    -> "Aa"
                                    1    -> "📄"
                                    else -> "🤖"
                                }
                            )
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Open $label screen"
                        }
                    )
                }
                // Settings gear – always visible
                NavigationBarItem(
                    selected = false,
                    onClick  = { showSettings = true },
                    label    = { Text("Settings") },
                    icon     = { Text("⚙") },
                    modifier = Modifier.semantics {
                        contentDescription = "Open Accessibility Settings"
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                0    -> AccessibleNoteScreenContent(speechEngine, ttsEngine, triggeredAction, onActionConsumed)
                1    -> DocumentLibraryScreen(ttsEngine = ttsEngine)
                else -> AiToolsScreen(ttsEngine)
            }
        }
    }
}
