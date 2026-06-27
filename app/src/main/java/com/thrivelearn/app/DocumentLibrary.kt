package com.thrivelearn.app

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
    // State to hold the list of chosen files (Name and Path)
    var selectedFiles by remember { mutableStateOf<List<Pair<String, Uri>>>(emptyList()) }
    val currentFontSize = (16 * fontScaleMultiplier).sp

    // SAF File Picker Intent
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(context, it) ?: "Unknown File"
            selectedFiles = selectedFiles + Pair(fileName, it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { 
                // arrayOf("*/*") allows selecting any file type. You can restrict this to "application/pdf" etc.
                filePickerLauncher.launch(arrayOf("*/*")) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .semantics {
                    contentDescription = "Open file browser to add a digital book or media file"
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

        // Accessible scrollable list of selected files
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedFiles) { file ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Next Step: Integrate PDF/Media Viewers here */ }
                        .semantics {
                            contentDescription = "Study material: ${file.first}. Double tap to open."
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

// Helper function to extract the real file name from Android's secure URI content provider
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