package com.thrivelearn.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class ThriveSpeechEngine(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        // FIXED: Stop any existing listener before starting a new one
        if (isListening) {
            stopListening()
        }

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {}
                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {}

                        override fun onError(error: Int) {
                            isListening = false
                            val message = when (error) {
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                                SpeechRecognizer.ERROR_NO_MATCH -> "Could not understand speech"
                                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                                SpeechRecognizer.ERROR_CLIENT -> "Client error"
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                                else -> "Speech engine stopped (Error: $error)"
                            }
                            onError(message)
                            // FIXED: Clean up resources on error
                            speechRecognizer?.destroy()
                            speechRecognizer = null
                        }

                        override fun onResults(results: Bundle?) {
                            isListening = false
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                onResult(matches[0])
                            }
                            // FIXED: Cleanup after results
                            cleanup()
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                onResult(matches[0])
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                }
                isListening = true
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                onError("Failed to initialize speech recognizer: ${e.message}")
                cleanup()
            }
        } else {
            onError("Native speech recognition is not available on this device.")
        }
    }

    fun stopListening() {
        // FIXED: Proper cleanup of speech recognizer
        isListening = false
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            // Ignore if already stopped
        }
        cleanup()
    }

    // FIXED: Separate cleanup method to ensure resources are freed
    private fun cleanup() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // Ignore
        }
        speechRecognizer = null
        isListening = false
    }
}
