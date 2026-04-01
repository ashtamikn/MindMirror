package com.mindmirror.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert
    suspend fun insert(entry: DiaryEntryEntity): Long

    @Update
    suspend fun update(entry: DiaryEntryEntity)

    @Query("SELECT * FROM diary_entries ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<DiaryEntryEntity>>
}

