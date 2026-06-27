package com.thrivelearn.app

import android.app.Application
import androidx.room.Room
import com.thrivelearn.app.database.ThriveLearnDatabase
import com.thrivelearn.app.preferences.SettingsPreferences
import com.thrivelearn.app.repository.NoteRepository

// FIXED: Application class for dependency injection setup
class ThriveLearnApp : Application() {
    companion object {
        lateinit var instance: ThriveLearnApp
            private set

        lateinit var database: ThriveLearnDatabase
            private set

        lateinit var noteRepository: NoteRepository
            private set

        lateinit var settingsPreferences: SettingsPreferences
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Room Database
        database = Room.databaseBuilder(
            applicationContext,
            ThriveLearnDatabase::class.java,
            ThriveLearnDatabase.DATABASE_NAME
        ).build()

        // Initialize Repository
        noteRepository = NoteRepository(database.noteDao())

        // Initialize Settings Preferences
        settingsPreferences = SettingsPreferences(applicationContext)
    }
}
