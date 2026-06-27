package com.thrivelearn.app

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticManager {
    // FIXED: Added different vibration patterns for accessibility feedback
    enum class FeedbackType {
        SUCCESS,    // Short vibration
        WARNING,    // Double vibration
        ERROR,      // Long vibration
        RECORDING,  // Rapid vibration
        CLICK       // Light tap
    }

    fun playVibration(context: Context, duration: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    // FIXED: Added pattern-based vibrations
    fun playPattern(context: Context, feedbackType: FeedbackType) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (feedbackType) {
                FeedbackType.SUCCESS -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE) // Short
                FeedbackType.WARNING -> {
                    val timings = longArrayOf(0, 100, 100, 100)
                    val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                }
                FeedbackType.ERROR -> VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE) // Long
                FeedbackType.RECORDING -> {
                    val timings = longArrayOf(0, 50, 30, 50)
                    val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                }
                FeedbackType.CLICK -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE) // Light
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val duration = when (feedbackType) {
                FeedbackType.SUCCESS -> 100L
                FeedbackType.WARNING -> 200L
                FeedbackType.ERROR -> 300L
                FeedbackType.RECORDING -> 150L
                FeedbackType.CLICK -> 50L
            }
            vibrator.vibrate(duration)
        }
    }
}
