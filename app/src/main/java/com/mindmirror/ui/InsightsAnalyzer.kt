package com.mindmirror.ui

import com.mindmirror.data.DiaryEntry

data class Insights(
    val summary: String,
    val whatWentWell: List<String>,
    val improvements: List<String>
)

data class ReflectionAdvice(
    val emotion: String,
    val summary: String,
    val guidance: List<String>
)

object InsightsAnalyzer {
    fun analyze(entries: List<DiaryEntry>): Insights {
        if (entries.isEmpty()) {
            return Insights(
                summary = "No entries yet. Write your first reflection today.",
                whatWentWell = emptyList(),
                improvements = listOf("Start with 3 lines: what happened, how you felt, what you learned.")
            )
        }

        val combined = entries.joinToString(" ") { it.content.lowercase() }
        val positiveWords = listOf("good", "happy", "grateful", "proud", "calm", "progress")
        val stressWords = listOf("stress", "anxious", "angry", "overwhelmed", "sad", "tired")

        val positives = mutableListOf<String>()
        val improvements = mutableListOf<String>()

        if (positiveWords.any { combined.contains(it) }) {
            positives += "You are noticing positive moments, which builds emotional resilience."
        } else {
            positives += "You are journaling consistently, which improves self-awareness over time."
        }

        if (stressWords.any { combined.contains(it) }) {
            improvements += "Add a short reset habit when stress appears (walk, breathing, or stretch for 10 minutes)."
        }

        improvements += "End each note with one action for tomorrow to turn reflection into progress."

        val summary = "You wrote ${entries.size} entries. Your reflections are becoming a useful pattern tracker for your mood and decisions."

        return Insights(
            summary = summary,
            whatWentWell = positives,
            improvements = improvements
        )
    }

    fun reflect(content: String, mood: String, recentEntries: List<DiaryEntry>): ReflectionAdvice {
        val cleaned = content.replace("\\s+".toRegex(), " ").trim()
        val normalized = cleaned.lowercase()
        if (cleaned.isBlank()) {
            return ReflectionAdvice(
                emotion = "Neutral",
                summary = "Write a few lines first, then I can summarize and guide you.",
                guidance = listOf("Start with: what happened, how you felt, and one next step.")
            )
        }

        val stressWords = listOf("stress", "anxious", "angry", "overwhelmed", "sad", "tired", "burnout")
        val positiveWords = listOf("good", "happy", "grateful", "proud", "calm", "progress", "confident")
        val overthinkWords = listOf("confused", "stuck", "worried", "uncertain", "panic")

        val hasStressSignal = stressWords.any { normalized.contains(it) }
        val hasPositiveSignal = positiveWords.any { normalized.contains(it) }
        val hasOverthinking = overthinkWords.any { normalized.contains(it) }

        val sentences = cleaned
            .split(Regex("(?<=[.!?])\\s+|\\n+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val thoughtSentence = sentences.maxByOrNull { sentence ->
            val lower = sentence.lowercase()
            val signalScore =
                stressWords.count { lower.contains(it) } +
                    positiveWords.count { lower.contains(it) } +
                    overthinkWords.count { lower.contains(it) }
            signalScore * 100 + sentence.length
        } ?: cleaned

        val moodPart = mood.trim().ifBlank { "unspecified" }
        val emotion = when {
            hasStressSignal && hasPositiveSignal -> "Mixed"
            hasStressSignal -> "Heavy"
            hasPositiveSignal -> "Positive"
            hasOverthinking -> "Overthinking"
            else -> "Reflective"
        }
        val summary = buildString {
            append("From your reflection: \"")
            append(thoughtSentence.take(180).trimEnd())
            append(if (thoughtSentence.length > 180) "...\"" else "\"")
            append(". ")
            append(
                when {
                    hasStressSignal && hasPositiveSignal -> "You seem to be balancing pressure and progress."
                    hasStressSignal -> "You seem under pressure right now."
                    hasPositiveSignal -> "You seem to be in a constructive phase."
                    else -> "You are reflecting thoughtfully on your day."
                }
            )
            if (moodPart != "unspecified") {
                append(" Mood noted: ")
                append(moodPart)
                append(".")
            }
        }

        val guidance = mutableListOf<String>()
        if (hasStressSignal) {
            guidance += "Do a 10-minute reset now: water, deep breathing, and a short walk."
            guidance += "Break your next task into one tiny step you can finish in 15 minutes."
        }
        if (hasOverthinking) {
            guidance += "Separate facts from fears: write 3 facts and 1 action you control today."
        }
        if (hasPositiveSignal) {
            guidance += "Capture what worked today so you can repeat it tomorrow."
        }

        val repeatedMoodCount = recentEntries
            .take(7)
            .count { it.mood.equals(moodPart, ignoreCase = true) && moodPart != "unspecified" }
        if (repeatedMoodCount >= 3) {
            guidance += "This mood appears often recently. Plan one supportive routine for this week."
        }

        guidance += "End this page with one sentence: 'Tomorrow I will ___ at ___.'"

        return ReflectionAdvice(
            emotion = emotion,
            summary = summary,
            guidance = guidance.distinct().take(4)
        )
    }
}

