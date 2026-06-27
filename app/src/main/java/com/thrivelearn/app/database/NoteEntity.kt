package com.thrivelearn.app.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// FIXED: Database entity for storing notes
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val color: String = "#6750A4" // Material 3 primary color
)
