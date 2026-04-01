package com.mindmirror.data

data class DiaryEntry(
    val id: Long,
    val content: String,
    val mood: String,
    val createdAtEpochMillis: Long
)

