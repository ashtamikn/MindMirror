package com.mindmirror

import com.mindmirror.data.DiaryEntry
import com.mindmirror.ui.InsightsAnalyzer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InsightsAnalyzerTest {
    @Test
    fun analyze_returnsHelpfulSummary() {
        val result = InsightsAnalyzer.analyze(
            listOf(
                DiaryEntry(
                    id = 1,
                    content = "I felt stressed but made good progress on my tasks.",
                    mood = "mixed",
                    createdAtEpochMillis = 1710000000000
                )
            )
        )

        assertTrue(result.summary.contains("1 entries"))
        assertTrue(result.improvements.isNotEmpty())
    }

    @Test
    fun reflect_returnsActionableGuidanceForStressSignals() {
        val content = "I am stressed and overwhelmed with deadlines. I still made progress on one task."
        val advice = InsightsAnalyzer.reflect(
            content = content,
            mood = "anxious",
            recentEntries = emptyList()
        )

        assertTrue(advice.summary.contains("From your reflection"))
        assertTrue(advice.summary.contains("overwhelmed with deadlines"))
        assertTrue(advice.guidance.isNotEmpty())
    }

    @Test
    fun reflect_returnsFallbackForBlankContent() {
        val advice = InsightsAnalyzer.reflect(content = "   ", mood = "", recentEntries = emptyList())

        assertTrue(advice.summary.contains("Write a few lines first"))
        assertFalse(advice.guidance.isEmpty())
    }
}

