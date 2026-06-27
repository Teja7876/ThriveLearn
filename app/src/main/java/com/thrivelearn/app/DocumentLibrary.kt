package com.thrivelearn.app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DocumentLibraryScreen(fontScaleMultiplier: Float) {
    val context = LocalContext.current
    var selectedFiles by remember { mutableStateOf<List<Pair<String, Uri>>>(emptyList()) }
    var activeMediaUri by remember { mutableStateOf<Uri?>(null) }
    var activeMediaTitle by remember { mutableStateOf("") }
    var activeDocumentUri by remember { mutableStateOf<Uri?>(null) }
    var activeDocumentTitle by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }

    val currentFontSize = (16 * fontScaleMultiplier).sp

    // FIXED: Restrict to accessible file types only
    val filePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val mimeType = context.contentResolver.getType(it) ?: ""
                // Only allow text, audio, and video files
                if (mimeType.startsWith("text/") || mimeType.startsWith("audio/") || mimeType.startsWith(
                        "video/"
                    )
                ) {
                    val fileName = getFileNameFromUri(context, it) ?: "Unknown File"
                    if (selectedFiles.none { file -> file.second == it }) {
                        selectedFiles = selectedFiles + Pair(fileName, it)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Only text, audio, and video files are supported",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
        return
    }

    if (activeDocumentUri != null) {
        AccessibleTextEditor(
            fileUri = activeDocumentUri!!,
            fileName = activeDocumentTitle,
            fontScaleMultiplier = fontScaleMultiplier,
            onClose = { activeDocumentUri = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .semantics { contentDescription = "Document library screen" }
    ) {
        // FIXED: Add proper semantics to buttons
        Button(
            onClick = { filePickerLauncher.launch(arrayOf("text/*", "audio/*", "video/*")) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .semantics { contentDescription = "Add file to library button" }
        ) {
            Text("Add File to Library", fontSize = currentFontSize)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showSettings = true },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Configure hardware buttons" }
        ) {
            Text("Configure Hardware Buttons", fontSize = currentFontSize)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeMediaUri != null) {
            AccessibleMediaPlayer(mediaUri = activeMediaUri!!, mediaTitle = activeMediaTitle)
            Button(
                onClick = { activeMediaUri = null },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Close media player" }
            ) {
                Text("Close Player")
            }
        }

        Text(
            "Your Study Materials",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { contentDescription = "Study materials list" }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(selectedFiles) { file ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            val mimeType = context.contentResolver.getType(file.second) ?: ""
                            when {
                                mimeType.startsWith("audio/") || mimeType.startsWith("video/") -> {
                                    activeMediaUri = file.second
                                    activeMediaTitle = file.first
                                }
                                mimeType.startsWith("text/") -> {
                                    activeDocumentUri = file.second
                                    activeDocumentTitle = file.first
                                }
                                else -> openUniversalFile(context, file.second)
                            }
                        }
                        .semantics { contentDescription = "File: ${file.first}" }
                ) {
                    Text(
                        text = file.first,
                        fontSize = currentFontSize,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var fileName: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName
}

private fun openUniversalFile(context: Context, uri: Uri) {
    val mimeType = context.contentResolver.getType(uri) ?: "*/*"
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No compatible application found.", Toast.LENGTH_LONG).show()
    }
}
