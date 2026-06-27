package com.thrivelearn.app.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// FIXED: DataStore for persistent settings
context(Context)
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsPreferences(private val context: Context) {
    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode") // NORMAL, HIGH_CONTRAST
        val FONT_MODE = stringPreferencesKey("font_mode")   // STANDARD, DYSLEXIA_FRIENDLY
        val DICTATE_KEY = stringPreferencesKey("dictate_key")
        val SAVE_KEY = stringPreferencesKey("save_key")
        val READ_ALOUD_KEY = stringPreferencesKey("read_aloud_key")
        val SPEECH_RATE = stringPreferencesKey("speech_rate")    // 0.5 to 2.0
        val TEXT_SIZE = stringPreferencesKey("text_size")        // 0.8 to 2.0
        val AUTO_SAVE_ENABLED = booleanPreferencesKey("auto_save")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")  // For onboarding
    }

    val themeMode: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "NORMAL"
    }

    val fontMode: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[FONT_MODE] ?: "STANDARD"
    }

    val dictateKey: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[DICTATE_KEY] ?: "VOLUME_UP"
    }

    val saveKey: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[SAVE_KEY] ?: "VOLUME_DOWN"
    }

    val readAloudKey: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[READ_ALOUD_KEY] ?: "POWER"
    }

    val speechRate: Flow<Float> = context.settingsDataStore.data.map { preferences ->
        preferences[SPEECH_RATE]?.toFloatOrNull() ?: 0.9f
    }

    val textSize: Flow<Float> = context.settingsDataStore.data.map { preferences ->
        preferences[TEXT_SIZE]?.toFloatOrNull() ?: 1.0f
    }

    val autoSaveEnabled: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[AUTO_SAVE_ENABLED] ?: true
    }

    val isFirstLaunch: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH] ?: true
    }

    // FIXED: Save individual settings
    suspend fun saveThemeMode(mode: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(THEME_MODE, mode)
            }
        }
    }

    suspend fun saveFontMode(mode: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(FONT_MODE, mode)
            }
        }
    }

    suspend fun saveSpeechRate(rate: Float) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(SPEECH_RATE, rate.toString())
            }
        }
    }

    suspend fun saveTextSize(size: Float) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(TEXT_SIZE, size.toString())
            }
        }
    }

    suspend fun setAutoSave(enabled: Boolean) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(AUTO_SAVE_ENABLED, enabled)
            }
        }
    }

    suspend fun setFirstLaunchComplete() {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(FIRST_LAUNCH, false)
            }
        }
    }

    suspend fun saveDictateKey(key: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(DICTATE_KEY, key)
            }
        }
    }

    suspend fun saveSaveKey(key: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(SAVE_KEY, key)
            }
        }
    }

    suspend fun saveReadAloudKey(key: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(READ_ALOUD_KEY, key)
            }
        }
    }
}
