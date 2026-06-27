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
import com.thrivelearn.app.theme.*

class MainActivity : ComponentActivity() {
    private lateinit var speechEngine: ThriveSpeechEngine
    private lateinit var ttsEngine: ThriveTextToSpeech
    private var volumeUpTriggerCount by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FORCE PORTRAIT for stable mounting on wheelchairs/desk-arms
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
                        MainScreenLayout(speechEngine, ttsEngine, volumeUpTriggerCount) { volumeUpTriggerCount++ }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeUpTriggerCount++
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
fun MainScreenLayout(speechEngine: ThriveSpeechEngine, ttsEngine: ThriveTextToSpeech, volumeTrigger: Int, onTrigger: () -> Unit) {
    val context = LocalContext.current
    var fontScale by remember { mutableFloatStateOf(1.0f) }
    var currentTab by remember { mutableIntStateOf(0) }
    val theme = LocalThemeMode.current
    
    // Global Gesture Handler: Left-Swipe (Tab Switch), Right-Swipe (Theme Toggle)
    Box(modifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures { _, dragAmount ->
            if (dragAmount > 50) { // Right Swipe -> Toggle Theme
                theme.value = if (theme.value == AppThemeMode.NORMAL) AppThemeMode.HIGH_CONTRAST else AppThemeMode.NORMAL
                HapticManager.playVibration(context, 100)
            } else if (dragAmount < -50) { // Left Swipe -> Toggle Tab
                currentTab = if (currentTab == 0) 1 else 0
                HapticManager.playVibration(context, 50)
            }
        }
    }) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(icon = { Text("??") }, label = { Text("Notes") }, selected = currentTab == 0, onClick = { currentTab = 0 })
                    NavigationBarItem(icon = { Text("??") }, label = { Text("Library") }, selected = currentTab == 1, onClick = { currentTab = 1 })
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                if (currentTab == 0) AccessibleNoteScreenContent(speechEngine, ttsEngine, fontScale, volumeTrigger)
                else DocumentLibraryScreen(fontScale)
            }
        }
    }
}
