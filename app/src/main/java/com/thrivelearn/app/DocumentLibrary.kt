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
fun DocumentLibraryScreen(
    fontScaleMultiplier: Float
) {
    val context = LocalContext.current
    var selectedFiles by remember { mutableStateOf<List<Pair<String, Uri>>>(emptyList()) }
    val currentFontSize = (16 * fontScaleMultiplier).sp

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(context, it) ?: "Unknown File"
            // Prevent duplicates
            if (selectedFiles.none { file -> file.second == it }) {
                selectedFiles = selectedFiles + Pair(fileName, it)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .semantics {
                    contentDescription = "Open file browser to add a digital book, document, or media file"
                    role = Role.Button
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Add File to Library", fontSize = currentFontSize)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Study Materials",
            style = MaterialTheme.typography.titleLarge,
            fontSize = (20 * fontScaleMultiplier).sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedFiles) { file ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openUniversalFile(context, file.second) }
                        .semantics {
                            contentDescription = "Study material: ${file.first}. Double tap to open in an external viewer."
                            role = Role.Button
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = file.first,
                        fontSize = currentFontSize,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

// Critical Engine: Routes any file type to the correct, accessible OS application
private fun openUniversalFile(context: Context, uri: Uri) {
    val mimeType = context.contentResolver.getType(uri) ?: "*/*"
    
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        // Required to grant the viewing app permission to read this specific file
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Fallback if the user does not have an app installed capable of opening the file (e.g., no PDF reader)
        Toast.makeText(context, "No compatible application found to open this file type.", Toast.LENGTH_LONG).show()
    }
}