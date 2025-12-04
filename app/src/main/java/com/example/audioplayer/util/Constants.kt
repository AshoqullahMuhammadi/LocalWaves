package com.example.audioplayer.util

object Constants {
    // Playback speeds
    val PLAYBACK_SPEEDS = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    
    // Default values
    const val DEFAULT_PLAYBACK_SPEED = 1.0f
    const val DEFAULT_CROSSFADE_DURATION = 0
    
    // Time constants
    const val POSITION_UPDATE_INTERVAL_MS = 200L
    const val SEEK_DEBOUNCE_MS = 100L
    
    // Notification
    const val NOTIFICATION_CHANNEL_ID = "localwave_playback_channel"
    const val NOTIFICATION_ID = 1
    
    // File types
    val SUPPORTED_AUDIO_FORMATS = listOf(
        "audio/mpeg",       // MP3
        "audio/mp4",        // M4A/AAC
        "audio/aac",        // AAC
        "audio/x-wav",      // WAV
        "audio/wav",        // WAV
        "audio/ogg",        // OGG
        "audio/flac",       // FLAC
        "audio/x-flac"      // FLAC
    )
    
    val SUPPORTED_EXTENSIONS = listOf(
        ".mp3",
        ".m4a",
        ".aac",
        ".wav",
        ".ogg",
        ".flac"
    )
}

