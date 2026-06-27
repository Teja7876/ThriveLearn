package com.thrivelearn.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thrivelearn.app.theme.*

class MainActivity : ComponentActivity() {
    private lateinit var speechEngine: ThriveSpeechEngine
    private lateinit var ttsEngine: ThriveTextToSpeech

    // State to trigger actions from hardware
    var triggeredAction by mutableStateOf<AppAction?>(null)

    // FIXED: Permission request launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(
                this,
                "Some permissions were denied. App functionality may be limited.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        try {
            speechEngine = ThriveSpeechEngine(applicationContext)
            ttsEngine = ThriveTextToSpeech(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to initialize app services", Toast.LENGTH_SHORT).show()
        }

        setContent {
            val currentTheme = remember { mutableStateOf(AppThemeMode.NORMAL) }
            val currentFont = remember { mutableStateOf(AppFontMode.STANDARD) }
            val permissionsGranted = remember { mutableStateOf(false) }
            val context = LocalContext.current

            // FIXED: Check permissions on first launch
            LaunchedEffect(Unit) {
                val granted = PermissionManager.areAllPermissionsGranted(context)
                permissionsGranted.value = granted
            }

            CompositionLocalProvider(
                LocalThemeMode provides currentTheme,
                LocalFontMode provides currentFont
            ) {
                ThriveLearnTheme(
                    themeMode = currentTheme.value,
                    fontMode = currentFont.value
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (permissionsGranted.value) {
                            MainScreenLayout(
                                speechEngine,
                                ttsEngine,
                                triggeredAction
                            ) { triggeredAction = null }
                        } else {
                            PermissionScreen(
                                onPermissionsGranted = {
                                    permissionsGranted.value = true
                                },
                                onRequestPermissions = { permissions ->
                                    permissionLauncher.launch(permissions.toTypedArray())
                                }
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

    // FIXED: Clean up speech engine resources
    override fun onPause() {
        try {
            speechEngine.stopListening()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onPause()
    }

    override fun onDestroy() {
        // FIXED: Proper cleanup of all resources
        try {
            speechEngine.stopListening()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            ttsEngine.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}

@Composable
fun MainScreenLayout(
    speechEngine: ThriveSpeechEngine,
    ttsEngine: ThriveTextToSpeech,
    triggeredAction: AppAction?,
    onActionConsumed: () -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(0) }

    // FIXED: Use Column to stack tab content and navigation
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            try {
                when (currentTab) {
                    0 -> AccessibleNoteScreenContent(
                        speechEngine,
                        ttsEngine,
                        triggeredAction,
                        onActionConsumed
                    )
                    1 -> DocumentLibraryScreen(1.0f)
                    2 -> SettingsScreen(onBack = {})
                    else -> AccessibleNoteScreenContent(
                        speechEngine,
                        ttsEngine,
                        triggeredAction,
                        onActionConsumed
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Error loading screen: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // FIXED: Tab navigation at bottom
        TabNavigationBar(
            currentTab = currentTab,
            onTabChange = { newTab ->
                try {
                    speechEngine.stopListening()
                } catch (e: Exception) {
                    // Ignore
                }
                currentTab = newTab
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
