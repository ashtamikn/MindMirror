package com.mindmirror.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiaryRepository(private val dao: DiaryDao) {
    fun observeEntries(): Flow<List<DiaryEntry>> {
        return dao.observeAll().map { entities ->
            entities.map { entity ->
                DiaryEntry(
                    id = entity.id,
                    content = entity.content,
                    mood = entity.mood,
                    createdAtEpochMillis = entity.createdAtEpochMillis
                )
            }
        }
    }

    suspend fun addEntry(content: String, mood: String): Long {
        return dao.insert(
            DiaryEntryEntity(
                content = content.trim(),
                mood = mood.trim().ifBlank { "unspecified" },
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun addEntryWithDate(content: String, mood: String, dateMillis: Long): Long {
        return dao.insert(
            DiaryEntryEntity(
                content = content.trim(),
                mood = mood.trim().ifBlank { "unspecified" },
                createdAtEpochMillis = dateMillis
            )
        )
    }

    suspend fun updateEntry(id: Long, content: String, mood: String, dateMillis: Long) {
        dao.update(
            DiaryEntryEntity(
                id = id,
                content = content.trim(),
                mood = mood.trim().ifBlank { "unspecified" },
                createdAtEpochMillis = dateMillis
            )
        )
    }
}

