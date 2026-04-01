package com.mindmirror.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DiaryEntryEntity::class], version = 1, exportSchema = false)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}

