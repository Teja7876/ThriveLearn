package com.thrivelearn.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

data class PermissionInfo(
    val permission: String,
    val displayName: String,
    val description: String,
    val isGranted: Boolean
)

object PermissionManager {
    // Essential permissions for ThriveLearn PWD app
    const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
    const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

    /**
     * Get all required permissions for the app
     */
    fun getRequiredPermissions(): List<String> {
        return listOf(
            RECORD_AUDIO,
            READ_EXTERNAL_STORAGE
            // WRITE_EXTERNAL_STORAGE is implicit for API 30+
        )
    }

    /**
     * Get permission info for display
     */
    fun getPermissionInfo(context: Context): List<PermissionInfo> {
        return listOf(
            PermissionInfo(
                permission = RECORD_AUDIO,
                displayName = "Microphone Access",
                description = "Required for voice dictation and speech-to-text features",
                isGranted = isPermissionGranted(context, RECORD_AUDIO)
            ),
            PermissionInfo(
                permission = READ_EXTERNAL_STORAGE,
                displayName = "File Access",
                description = "Required to read and open study materials (documents, audio, video)",
                isGranted = isPermissionGranted(context, READ_EXTERNAL_STORAGE)
            )
        )
    }

    /**
     * Check if a single permission is granted
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if all required permissions are granted
     */
    fun areAllPermissionsGranted(context: Context): Boolean {
        return getRequiredPermissions().all { isPermissionGranted(context, it) }
    }

    /**
     * Get list of permissions that still need to be requested
     */
    fun getPermissionsToRequest(context: Context): List<String> {
        return getRequiredPermissions().filter { !isPermissionGranted(context, it) }
    }
}
