package com.thrivelearn.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thrivelearn.app.theme.*

class MainActivity : ComponentActivity() {
    private lateinit var speechEngine: ThriveSpeechEngine
    private lateinit var ttsEngine: ThriveTextToSpeech
    
    // State to trigger actions from hardware
    var triggeredAction by mutableStateOf<AppAction?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
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
        ttsEngine.shutdown()
        super.onDestroy()
    }
}

@Composable
fun MainScreenLayout(speechEngine: ThriveSpeechEngine, ttsEngine: ThriveTextToSpeech, triggeredAction: AppAction?, onActionConsumed: () -> Unit) {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(0) }
    
    // Pass the trigger down to the workspace
    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (currentTab == 0) AccessibleNoteScreenContent(speechEngine, ttsEngine, triggeredAction, onActionConsumed)
            else DocumentLibraryScreen(1.0f)
        }
    }
}
