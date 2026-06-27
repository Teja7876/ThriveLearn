package com.thrivelearn.app

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@Composable
fun AccessibleTextEditor(
    fileUri: Uri,
    fileName: String,
    fontScaleMultiplier: Float,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var documentText by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val currentFontSize = (16 * fontScaleMultiplier).sp

    // Load file contents when component launches
    LaunchedEffect(fileUri) {
        try {
            isLoading = true
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    documentText = reader.readText()
                }
            }
            isLoading = false
        } catch (e: SecurityException) {
            // FIXED: Handle permission errors
            errorMessage = "Permission denied: Cannot read this file"
            isLoading = false
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            onClose()
        } catch (e: Exception) {
            // FIXED: Better error handling
            errorMessage = "Failed to read file: ${e.message}"
            isLoading = false
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            onClose()
        }
    }

    // FIXED: Show loading state
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = "Loading document" }
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .semantics { contentDescription = "Text Editor for $fileName" }
    ) {
        Text(
            text = "Editing: $fileName",
            style = MaterialTheme.typography.titleMedium,
            fontSize = (18 * fontScaleMultiplier).sp,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .semantics { contentDescription = "File name: $fileName" }
        )

        OutlinedTextField(
            value = documentText,
            onValueChange = { documentText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .semantics { contentDescription = "Document content. Type to edit." },
            textStyle = LocalTextStyle.current.copy(fontSize = currentFontSize),
            placeholder = { Text("Document content will appear here...") },
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .semantics { contentDescription = "Close editor button" }
            ) {
                Text("Close", fontSize = currentFontSize)
            }

            Button(
                onClick = {
                    isSaving = true
                    try {
                        // FIXED: Handle write permissions properly
                        context.contentResolver.openOutputStream(fileUri, "wt")?.use { outputStream ->
                            OutputStreamWriter(outputStream).use { writer ->
                                writer.write(documentText)
                            }
                        }
                        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: SecurityException) {
                        // FIXED: Handle write permission denied
                        Toast.makeText(
                            context,
                            "Permission denied: Cannot save to this location",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        // FIXED: Better error handling
                        Toast.makeText(
                            context,
                            "Failed to save file: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        isSaving = false
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .semantics { contentDescription = "Save changes to document" }
            ) {
                Text(if (isSaving) "Saving..." else "Save Changes", fontSize = currentFontSize)
            }
        }
    }
}
