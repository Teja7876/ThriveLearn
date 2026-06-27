package com.thrivelearn.app.repository

import com.thrivelearn.app.database.NoteDao
import com.thrivelearn.app.database.NoteEntity
import kotlinx.coroutines.flow.Flow

// FIXED: Repository pattern for notes data access
class NoteRepository(private val noteDao: NoteDao) {
    // Get all notes
    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    // Get favorite notes
    fun getFavoriteNotes(): Flow<List<NoteEntity>> = noteDao.getFavoriteNotes()

    // Get note by ID
    fun getNoteById(id: Long): Flow<NoteEntity?> = noteDao.getNoteById(id)

    // Search notes
    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes("%$query%")

    // Get note count
    fun getNoteCount(): Flow<Int> = noteDao.getNoteCount()

    // Insert note and return ID
    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

    // Update note
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    // Delete note
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    // Delete note by ID
    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    // Create new note with dictated text
    suspend fun createNoteFromDictation(title: String, content: String): Long {
        val note = NoteEntity(
            title = title,
            content = content,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return insertNote(note)
    }

    // Quick save note (for auto-save feature)
    suspend fun quickSaveNote(id: Long, content: String) {
        val note = NoteEntity(
            id = id,
            title = "Quick Note - ${java.text.SimpleDateFormat("MMM dd").format(System.currentTimeMillis())}",
            content = content,
            updatedAt = System.currentTimeMillis()
        )
        updateNote(note)
    }

    // Toggle favorite
    suspend fun toggleFavorite(note: NoteEntity) {
        updateNote(note.copy(isFavorite = !note.isFavorite))
    }
}
