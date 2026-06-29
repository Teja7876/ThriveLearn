package com.thrivelearn.app

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
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
    ttsEngine: ThriveTextToSpeech? = null,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var documentText by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showFocusView by remember { mutableStateOf(false) }
    var showReviewQuestions by remember { mutableStateOf(false) }
    val currentFontSize = (16 * fontScaleMultiplier).sp

    // Load file contents when component launches
    LaunchedEffect(fileUri) {
        try {
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    documentText = reader.readText()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to read file", Toast.LENGTH_SHORT).show()
            onClose()
        }
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
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = documentText,
            onValueChange = { documentText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .semantics { contentDescription = "Document content. Type to edit." },
            textStyle = LocalTextStyle.current.copy(fontSize = currentFontSize)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { ttsEngine?.speak(documentText.ifBlank { "This document is empty." }) },
                enabled = ttsEngine != null,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Read", fontSize = currentFontSize)
            }

            OutlinedButton(
                onClick = {
                    if (documentText.trim().length > 100) {
                        documentText = TextSummarizer.summarize(documentText)
                        Toast.makeText(context, "Document simplified", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Text too short to simplify", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Simplify", fontSize = currentFontSize)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showFocusView = !showFocusView },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showFocusView) "Hide Focus View" else "Show Focus View", fontSize = currentFontSize)
        }

        OutlinedButton(
            onClick = { showReviewQuestions = !showReviewQuestions },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(if (showReviewQuestions) "Hide Review Questions" else "Create Review Questions", fontSize = currentFontSize)
        }

        if (showFocusView) {
            StudyListPanel("Reading chunks", StudyAids.readingChunks(documentText), Modifier.padding(top = 8.dp), currentFontSize)
        }

        if (showReviewQuestions) {
            StudyListPanel("Review questions", StudyAids.reviewQuestions(documentText), Modifier.padding(top = 8.dp), currentFontSize)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Close", fontSize = currentFontSize)
            }

            Button(
                onClick = {
                    isSaving = true
                    try {
                        context.contentResolver.openOutputStream(fileUri, "wt")?.use { outputStream ->
                            OutputStreamWriter(outputStream).use { writer ->
                                writer.write(documentText)
                            }
                        }
                        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
                    } finally {
                        isSaving = false
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.weight(1f).padding(start = 8.dp).semantics { contentDescription = "Save changes to document" }
            ) {
                Text(if (isSaving) "Saving..." else "Save Changes", fontSize = currentFontSize)
            }
        }
    }
}
