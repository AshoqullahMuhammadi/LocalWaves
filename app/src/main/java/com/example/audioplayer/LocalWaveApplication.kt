package com.example.audioplayer

import android.app.Application
import com.example.audioplayer.data.local.db.AppDatabase
import com.example.audioplayer.data.local.preferences.UserPreferences
import com.example.audioplayer.data.repository.MediaRepository
import com.example.audioplayer.player.PlayerController

class LocalWaveApplication : Application() {
    
    // Lazy initialization to avoid blocking main thread
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
    
    val mediaRepository: MediaRepository by lazy {
        MediaRepository(
            context = this,
            trackDao = database.trackDao(),
            albumDao = database.albumDao(),
            artistDao = database.artistDao(),
            playlistDao = database.playlistDao(),
            queueDao = database.queueDao(),
            playbackStateDao = database.playbackStateDao()
        )
    }
    
    val userPreferences: UserPreferences by lazy {
        UserPreferences(this)
    }
    
    val playerController: PlayerController by lazy {
        PlayerController(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        // Don't initialize anything heavy here - use lazy initialization
    }
}

val Application.localWaveApp: LocalWaveApplication
    get() = this as LocalWaveApplication
