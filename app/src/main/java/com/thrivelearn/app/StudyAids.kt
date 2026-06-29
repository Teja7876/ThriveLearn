package com.thrivelearn.app

object StudyAids {
    private val commonWords = setOf(
        "about", "after", "again", "also", "because", "before", "being", "between", "could",
        "every", "from", "have", "into", "more", "other", "should", "some", "than", "that",
        "their", "there", "these", "they", "this", "through", "under", "when", "where",
        "which", "while", "with", "would", "your"
    )

    fun readingChunks(text: String, maxChunks: Int = 6): List<String> {
        val sentences = text
            .split(Regex("(?<=[.!?])\\s+|\\n+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (sentences.isEmpty()) return emptyList()

        val chunks = mutableListOf<String>()
        var current = StringBuilder()

        sentences.forEach { sentence ->
            if (current.length + sentence.length > 180 && current.isNotBlank()) {
                chunks += current.toString()
                current = StringBuilder()
            }
            if (current.isNotBlank()) current.append(" ")
            current.append(sentence)
        }

        if (current.isNotBlank()) chunks += current.toString()
        return chunks.take(maxChunks)
    }

    fun keyPoints(text: String, maxPoints: Int = 4): List<String> {
        return TextSummarizer
            .summarize(text, maxPoints)
            .split(Regex("(?<=[.!?])\\s+|\\n+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(maxPoints)
    }

    fun studyChecklist(text: String, maxItems: Int = 5): List<String> {
        return keyPoints(text, maxItems).map { point ->
            point
                .removePrefix("-")
                .trim()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    fun reviewQuestions(text: String, maxQuestions: Int = 4): List<String> {
        return keyPoints(text, maxQuestions).map { point ->
            val cleaned = point.trim().trimEnd('.', '!', '?')
            "What should you remember about $cleaned?"
        }
    }

    fun flashcards(text: String, maxCards: Int = 6): List<Flashcard> {
        val candidates = importantTerms(text, maxCards)
        val sentences = sentences(text)

        return candidates.map { term ->
            val evidence = sentences.firstOrNull { it.contains(term, ignoreCase = true) }
            Flashcard(
                front = "What does \"$term\" mean here?",
                back = evidence ?: "Review where \"$term\" appears in the note."
            )
        }
    }

    fun glossary(text: String, maxTerms: Int = 8): List<GlossaryEntry> {
        val sentences = sentences(text)
        return importantTerms(text, maxTerms).map { term ->
            GlossaryEntry(
                term = term,
                context = sentences.firstOrNull { it.contains(term, ignoreCase = true) }
                    ?: "Find examples of \"$term\" in your material."
            )
        }
    }

    fun plainLanguage(text: String): String {
        val replacements = mapOf(
            "utilize" to "use",
            "demonstrate" to "show",
            "indicate" to "show",
            "approximately" to "about",
            "commence" to "start",
            "terminate" to "end",
            "subsequent" to "next",
            "prior" to "before",
            "sufficient" to "enough",
            "methodology" to "method"
        )

        var simplified = text
        replacements.forEach { (hard, easy) ->
            simplified = simplified.replace(Regex("\\b$hard\\b", RegexOption.IGNORE_CASE), easy)
        }
        return readingChunks(simplified, maxChunks = 8).joinToString("\n\n")
    }

    fun readingDifficulty(text: String): ReadingDifficulty {
        val words = words(text)
        val sentenceCount = sentences(text).size.coerceAtLeast(1)
        val longWordCount = words.count { it.length >= 8 }
        val averageSentenceLength = if (words.isEmpty()) 0 else words.size / sentenceCount
        val longWordPercent = if (words.isEmpty()) 0 else (longWordCount * 100) / words.size
        val level = when {
            words.isEmpty() -> "No text yet"
            averageSentenceLength > 24 || longWordPercent > 28 -> "Hard"
            averageSentenceLength > 16 || longWordPercent > 18 -> "Medium"
            else -> "Easy"
        }

        val suggestion = when (level) {
            "Hard" -> "Use Focus View, Plain Language, and Read Aloud before studying."
            "Medium" -> "Use Key Points and Quiz Me to check understanding."
            "Easy" -> "This text is already fairly readable."
            else -> "Add study text to analyze it."
        }

        return ReadingDifficulty(level, averageSentenceLength, longWordPercent, suggestion)
    }

    private fun sentences(text: String): List<String> {
        return text
            .split(Regex("(?<=[.!?])\\s+|\\n+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    private fun words(text: String): List<String> {
        return text
            .lowercase()
            .split(Regex("\\W+"))
            .filter { it.length > 2 }
    }

    private fun importantTerms(text: String, maxTerms: Int): List<String> {
        return words(text)
            .filter { it.length >= 5 && it !in commonWords }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { it.key }
            .take(maxTerms)
    }
}

data class Flashcard(val front: String, val back: String)
data class GlossaryEntry(val term: String, val context: String)
data class ReadingDifficulty(
    val level: String,
    val averageSentenceLength: Int,
    val longWordPercent: Int,
    val suggestion: String
)
