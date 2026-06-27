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
fun DocumentLibraryScreen(fontScaleMultiplier: Float = 1.0f, ttsEngine: ThriveTextToSpeech? = null) {
    val context = LocalContext.current
    var selectedFiles by remember { mutableStateOf<List<Pair<String, Uri>>>(emptyList()) }
    var activeMediaUri by remember { mutableStateOf<Uri?>(null) }
    var activeMediaTitle by remember { mutableStateOf("") }
    var activeDocumentUri by remember { mutableStateOf<Uri?>(null) }
    var activeDocumentTitle by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }
    
    val currentFontSize = (16 * fontScaleMultiplier).sp

    val filePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                try {
                    context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: SecurityException) {
                    // Some providers do not offer persistable access; the file still works in this session.
                }
            }
            val fileName = getFileNameFromUri(context, it) ?: "Unknown File"
            if (selectedFiles.none { file -> file.second == it }) { selectedFiles = selectedFiles + Pair(fileName, it) }
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
            ttsEngine = ttsEngine,
            onClose = { activeDocumentUri = null }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Study Materials", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Add text, audio, video, PDFs, or other class files.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Button(onClick = { filePickerLauncher.launch(arrayOf("text/*", "audio/*", "video/*", "application/pdf", "application/*")) }, modifier = Modifier.fillMaxWidth().height(64.dp)) { Text("Add File to Library", fontSize = currentFontSize) }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(onClick = { showSettings = true }, modifier = Modifier.fillMaxWidth()) { Text("Configure Hardware Buttons", fontSize = currentFontSize) }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeMediaUri != null) {
            AccessibleMediaPlayer(mediaUri = activeMediaUri!!, mediaTitle = activeMediaTitle)
            Button(onClick = { activeMediaUri = null }, modifier = Modifier.fillMaxWidth()) { Text("Close Player") }
        }

        Text("Your Study Materials", style = MaterialTheme.typography.titleLarge)
        if (selectedFiles.isEmpty()) {
            Text(
                "No files added yet.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(selectedFiles) { file ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                    val mimeType = context.contentResolver.getType(file.second) ?: ""
                    if (mimeType.startsWith("audio/") || mimeType.startsWith("video/")) {
                        activeMediaUri = file.second
                        activeMediaTitle = file.first
                    } else if (mimeType.startsWith("text/")) {
                        activeDocumentUri = file.second
                        activeDocumentTitle = file.first
                    } else {
                        openUniversalFile(context, file.second)
                    }
                }.semantics { contentDescription = "Open study material ${file.first}" }) {
                    Text(text = file.first, fontSize = currentFontSize, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var fileName: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use { if (it.moveToFirst()) { val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME); if (nameIndex >= 0) { fileName = it.getString(nameIndex) } } }
    return fileName
}

private fun openUniversalFile(context: Context, uri: Uri) {
    val mimeType = context.contentResolver.getType(uri) ?: "*/*"
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try { context.startActivity(intent) } catch (e: ActivityNotFoundException) { Toast.makeText(context, "No compatible application found.", Toast.LENGTH_LONG).show() }
}
