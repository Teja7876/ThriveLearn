package com.thrivelearn.app

import android.view.KeyEvent

enum class AppAction { DICTATE, SAVE, READ_ALOUD }

object HardwareActionManager {
    // Default mappings
    var dictateKey: Int = KeyEvent.KEYCODE_VOLUME_UP
    var saveKey: Int = KeyEvent.KEYCODE_VOLUME_DOWN

    fun getActionForKey(keyCode: Int): AppAction? {
        return when (keyCode) {
            dictateKey -> AppAction.DICTATE
            saveKey -> AppAction.SAVE
            else -> null
        }
    }
}
