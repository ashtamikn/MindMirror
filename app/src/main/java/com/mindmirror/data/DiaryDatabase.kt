package com.mindmirror.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DiaryEntryEntity::class, TodoEntity::class, ThoughtEntity::class],
    version = 2,
    exportSchema = false
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun todoDao(): TodoDao
    abstract fun thoughtDao(): ThoughtDao
}

