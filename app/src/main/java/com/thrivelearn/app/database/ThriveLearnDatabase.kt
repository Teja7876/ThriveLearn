package com.thrivelearn.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// FIXED: Room database for storing notes
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ThriveLearnDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "thrivelearn.db"
    }
}
