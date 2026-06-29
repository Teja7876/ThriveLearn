package com.thrivelearn.app

import android.view.KeyEvent

enum class AppAction { DICTATE, SAVE, READ_ALOUD }

object HardwareActionManager {
    // Default mappings
    var dictateKey: Int = KeyEvent.KEYCODE_VOLUME_UP
    var saveKey: Int = KeyEvent.KEYCODE_VOLUME_DOWN
    var readAloudKey: Int = KeyEvent.KEYCODE_HEADSETHOOK

    fun getActionForKey(keyCode: Int): AppAction? {
        return when (keyCode) {
            dictateKey -> AppAction.DICTATE
            saveKey -> AppAction.SAVE
            readAloudKey -> AppAction.READ_ALOUD
            else -> null
        }
    }
}
