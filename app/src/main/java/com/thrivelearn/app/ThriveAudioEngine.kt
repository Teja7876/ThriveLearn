package com.thrivelearn.app

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class ThriveAudioEngine(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null

    fun startVoiceRecording(outputFile: File) {
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION") MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(96000)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }

    fun stopVoiceRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }
}