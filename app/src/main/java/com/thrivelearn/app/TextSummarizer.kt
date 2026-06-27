package com.thrivelearn.app

object TextSummarizer {
    fun summarize(text: String, sentenceCount: Int = 3): String {
        val sentences = text.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
        if (sentences.size <= sentenceCount) return text

        // Score sentences based on word frequency (a simplified TextRank approach)
        val words = text.lowercase().split(Regex("\\W+")).filter { it.length > 3 }
        val wordFreq = words.groupingBy { it }.eachCount()

        val rankedSentences = sentences.map { sentence ->
            val score = sentence.lowercase().split(Regex("\\W+")).sumOf { wordFreq[it] ?: 0 }
            sentence to score
        }.sortedByDescending { it.second }

        // Take top sentences and sort them back into original order
        return rankedSentences.take(sentenceCount)
            .sortedBy { sentences.indexOf(it.first) }
            .joinToString(" ") { it.first }
    }
}
