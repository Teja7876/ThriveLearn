package com.thrivelearn.app

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class ThriveTextToSpeech(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = true
                // FIXED: Set reasonable speech rate and pitch for accessibility
                tts?.setSpeechRate(0.9f) // Slightly slower for clarity
                tts?.setPitch(1.0f)
            }
        }
    }

    fun speak(text: String) {
        if (isInitialized && text.isNotBlank()) {
            // FIXED: Use QUEUE_FLUSH to ensure new text is played
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        if (isInitialized) {
            try {
                tts?.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isInitialized = false
    }
}
