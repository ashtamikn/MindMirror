package com.mindmirror.ai

import com.mindmirror.data.DiaryEntry
import com.mindmirror.ui.ReflectionAdvice

interface CompanionAi {
    suspend fun generateChatReply(content: String, mood: String, recentEntries: List<DiaryEntry>): String?

    suspend fun generateReflection(content: String, mood: String, recentEntries: List<DiaryEntry>): ReflectionAdvice?
}

