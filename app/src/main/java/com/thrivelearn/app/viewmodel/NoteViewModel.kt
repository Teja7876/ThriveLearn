package com.thrivelearn.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thrivelearn.app.database.NoteEntity
import com.thrivelearn.app.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// FIXED: ViewModel for notes management with coroutines
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    // All notes
    val allNotes: Flow<List<NoteEntity>> = repository.getAllNotes()

    // Favorite notes
    val favoriteNotes: Flow<List<NoteEntity>> = repository.getFavoriteNotes()

    // Note count
    val noteCount: Flow<Int> = repository.getNoteCount()

    // State for current note being edited
    private val _currentNote = MutableStateFlow<NoteEntity?>(null)
    val currentNote: StateFlow<NoteEntity?> = _currentNote.asStateFlow()

    // State for UI feedback
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    /**
     * Load note by ID
     */
    fun loadNote(id: Long) {
        viewModelScope.launch {
            repository.getNoteById(id).collect { note ->
                _currentNote.value = note
            }
        }
    }

    /**
     * Create new note from dictation
     */
    fun createNoteFromDictation(title: String, content: String) {
        viewModelScope.launch {
            try {
                val noteId = repository.createNoteFromDictation(title, content)
                _uiMessage.value = "Note saved (ID: $noteId)"
                _currentNote.value = NoteEntity(
                    id = noteId,
                    title = title,
                    content = content
                )
            } catch (e: Exception) {
                _uiMessage.value = "Failed to save note: ${e.message}"
            }
        }
    }

    /**
     * Update current note
     */
    fun updateCurrentNote(title: String, content: String) {
        viewModelScope.launch {
            try {
                _currentNote.value?.let { note ->
                    val updatedNote = note.copy(
                        title = title,
                        content = content,
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.updateNote(updatedNote)
                    _currentNote.value = updatedNote
                    _uiMessage.value = "Note updated"
                }
            } catch (e: Exception) {
                _uiMessage.value = "Failed to update note: ${e.message}"
            }
        }
    }

    /**
     * Auto-save note
     */
    fun autoSaveNote(content: String) {
        viewModelScope.launch {
            try {
                _currentNote.value?.let { note ->
                    val updatedNote = note.copy(
                        content = content,
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.updateNote(updatedNote)
                    _currentNote.value = updatedNote
                }
            } catch (e: Exception) {
                // Silent fail for auto-save
            }
        }
    }

    /**
     * Delete current note
     */
    fun deleteCurrentNote() {
        viewModelScope.launch {
            try {
                _currentNote.value?.let { note ->
                    repository.deleteNote(note)
                    _currentNote.value = null
                    _uiMessage.value = "Note deleted"
                }
            } catch (e: Exception) {
                _uiMessage.value = "Failed to delete note: ${e.message}"
            }
        }
    }

    /**
     * Toggle favorite
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                _currentNote.value?.let { note ->
                    repository.toggleFavorite(note)
                    _currentNote.value = note.copy(isFavorite = !note.isFavorite)
                }
            } catch (e: Exception) {
                _uiMessage.value = "Failed to toggle favorite: ${e.message}"
            }
        }
    }

    /**
     * Search notes
     */
    fun searchNotes(query: String): Flow<List<NoteEntity>> = repository.searchNotes(query)

    /**
     * Clear UI message
     */
    fun clearMessage() {
        _uiMessage.value = null
    }

    /**
     * ViewModel Factory
     */
    companion object Factory {
        fun create(repository: NoteRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteViewModel(repository) as T
            }
        }
    }
}
