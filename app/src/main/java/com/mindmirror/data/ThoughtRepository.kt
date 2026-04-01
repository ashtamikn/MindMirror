package com.mindmirror.data

import kotlinx.coroutines.flow.Flow

class ThoughtRepository(private val thoughtDao: ThoughtDao) {
    
    fun getAllThoughts(): Flow<List<ThoughtEntity>> = thoughtDao.getAllThoughts()

    suspend fun addThought(text: String): Long {
        return thoughtDao.insert(ThoughtEntity(text = text))
    }

    suspend fun updateThought(thought: ThoughtEntity) {
        thoughtDao.update(thought)
    }

    suspend fun deleteThought(thought: ThoughtEntity) {
        thoughtDao.delete(thought)
    }

    suspend fun deleteThoughtById(id: Long) {
        thoughtDao.deleteById(id)
    }
}
