package com.thrivelearn.app

import android.view.KeyEvent

enum class AppAction { DICTATE, SAVE, READ_ALOUD }

object HardwareActionManager {
    // FIXED: Default mappings with clear documentation
    /**
     * Volume Up: Start/Stop Dictation
     * Volume Down: Save current note
     */
    var dictateKey: Int = KeyEvent.KEYCODE_VOLUME_UP
    var saveKey: Int = KeyEvent.KEYCODE_VOLUME_DOWN
    var readAloudKey: Int = KeyEvent.KEYCODE_POWER // Power button to read aloud

    fun getActionForKey(keyCode: Int): AppAction? {
        return when (keyCode) {
            dictateKey -> AppAction.DICTATE
            saveKey -> AppAction.SAVE
            readAloudKey -> AppAction.READ_ALOUD
            else -> null
        }
    }

    // FIXED: Allow customization of key mappings
    fun remapKey(action: AppAction, keyCode: Int) {
        when (action) {
            AppAction.DICTATE -> dictateKey = keyCode
            AppAction.SAVE -> saveKey = keyCode
            AppAction.READ_ALOUD -> readAloudKey = keyCode
        }
    }
}
