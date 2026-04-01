package com.mindmirror.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ThoughtDao {
    @Query("SELECT * FROM thoughts ORDER BY createdAt DESC")
    fun getAllThoughts(): Flow<List<ThoughtEntity>>

    @Insert
    suspend fun insert(thought: ThoughtEntity): Long

    @Update
    suspend fun update(thought: ThoughtEntity)

    @Delete
    suspend fun delete(thought: ThoughtEntity)

    @Query("DELETE FROM thoughts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
