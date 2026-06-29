package com.thrivelearn.app

import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AiToolsScreen(ttsEngine: ThriveTextToSpeech) {
    val context = LocalContext.current
    val savedNotes = remember { context.getSharedPreferences("study_notes", 0) }
    var sourceText by remember { mutableStateOf(savedNotes.getString("latest_note", "") ?: "") }
    var outputMode by remember { mutableStateOf(AiToolMode.FLASHCARDS) }
    val difficulty = StudyAids.readingDifficulty(sourceText)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Free AI Study Tools", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Works offline using local study helpers. Paste text or load your saved note.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    sourceText = savedNotes.getString("latest_note", "") ?: ""
                    Toast.makeText(context, "Loaded latest saved note", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f).heightIn(min = 56.dp)
            ) { Text("Load Note") }

            OutlinedButton(
                onClick = { ttsEngine.speak(sourceText.ifBlank { "Add text first." }) },
                modifier = Modifier.weight(1f).heightIn(min = 56.dp)
            ) { Text("Read") }
        }

        OutlinedTextField(
            value = sourceText,
            onValueChange = { sourceText = it },
            label = { Text("Study text") },
            minLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .semantics { contentDescription = "Text for free AI study tools" }
        )

        DifficultyCard(difficulty)

        Text("Choose a tool", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
        ToolButtonGrid(selected = outputMode, onSelected = { outputMode = it })

        when (outputMode) {
            AiToolMode.FLASHCARDS -> FlashcardsPanel(StudyAids.flashcards(sourceText))
            AiToolMode.GLOSSARY -> GlossaryPanel(StudyAids.glossary(sourceText))
            AiToolMode.PLAIN_LANGUAGE -> StudyTextPanel("Plain language", StudyAids.plainLanguage(sourceText), Modifier.padding(top = 16.dp))
            AiToolMode.REVIEW_QUESTIONS -> StudyListPanel("Review questions", StudyAids.reviewQuestions(sourceText), Modifier.padding(top = 16.dp))
            AiToolMode.STUDY_PLAN -> StudyListPanel("Study plan", StudyAids.studyChecklist(sourceText), Modifier.padding(top = 16.dp))
            AiToolMode.OPEN_SOURCE -> OpenSourceAiPanel()
        }
    }
}

@Composable
private fun DifficultyCard(difficulty: ReadingDifficulty) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Reading level: ${difficulty.level}", style = MaterialTheme.typography.titleMedium)
            Text("Average sentence length: ${difficulty.averageSentenceLength} words")
            Text("Long words: ${difficulty.longWordPercent}%")
            Text(difficulty.suggestion, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun ToolButtonGrid(selected: AiToolMode, onSelected: (AiToolMode) -> Unit) {
    val modes = listOf(
        AiToolMode.FLASHCARDS,
        AiToolMode.GLOSSARY,
        AiToolMode.PLAIN_LANGUAGE,
        AiToolMode.REVIEW_QUESTIONS,
        AiToolMode.STUDY_PLAN,
        AiToolMode.OPEN_SOURCE
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
        modes.chunked(2).forEach { rowModes ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                rowModes.forEach { mode ->
                    val buttonModifier = Modifier.weight(1f).heightIn(min = 56.dp)
                    if (mode == selected) {
                        Button(onClick = { onSelected(mode) }, modifier = buttonModifier) { Text(mode.label) }
                    } else {
                        OutlinedButton(onClick = { onSelected(mode) }, modifier = buttonModifier) { Text(mode.label) }
                    }
                }
                if (rowModes.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FlashcardsPanel(cards: List<Flashcard>) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text("Flashcards", style = MaterialTheme.typography.titleLarge)
        if (cards.isEmpty()) {
            Text("Add more study text to create flashcards.", modifier = Modifier.padding(top = 8.dp))
        } else {
            cards.forEachIndexed { index, card ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${index + 1}. ${card.front}", style = MaterialTheme.typography.titleMedium)
                        Text(card.back, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun GlossaryPanel(entries: List<GlossaryEntry>) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text("Glossary", style = MaterialTheme.typography.titleLarge)
        if (entries.isEmpty()) {
            Text("Add more study text to find terms.", modifier = Modifier.padding(top = 8.dp))
        } else {
            entries.forEach { entry ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(entry.term.replaceFirstChar { it.titlecase() }, style = MaterialTheme.typography.titleMedium)
                        Text(entry.context, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun OpenSourceAiPanel() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text("Open-source AI engines", style = MaterialTheme.typography.titleLarge)
        ResourceCard(
            name = "Vosk",
            use = "Offline speech recognition for dictation without sending audio to a server.",
            status = "Recommended next integration after Gradle wrapper is restored."
        )
        ResourceCard(
            name = "Tesseract4Android",
            use = "OCR for turning textbook photos, printed handouts, or screenshots into readable text.",
            status = "Needs JitPack repository, dependency, and traineddata files."
        )
        ResourceCard(
            name = "Apache OpenNLP",
            use = "Language processing tasks like sentence detection and term extraction.",
            status = "Current app uses lightweight local helpers until dependencies can be verified."
        )
    }
}

@Composable
private fun ResourceCard(name: String, use: String, status: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text(use, modifier = Modifier.padding(top = 4.dp))
            Text(status, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

private enum class AiToolMode(val label: String) {
    FLASHCARDS("Flashcards"),
    GLOSSARY("Glossary"),
    PLAIN_LANGUAGE("Plain Text"),
    REVIEW_QUESTIONS("Quiz"),
    STUDY_PLAN("Plan"),
    OPEN_SOURCE("Free Engines")
}

