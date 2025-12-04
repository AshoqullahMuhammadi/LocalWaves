package com.example.audioplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_state")
data class PlaybackState(
    @PrimaryKey
    val id: Int = 1, // Single row table
    val currentTrackId: Long? = null,
    val currentPosition: Long = 0,
    val repeatMode: Int = RepeatMode.OFF.ordinal,
    val shuffleEnabled: Boolean = false,
    val playbackSpeed: Float = 1.0f
)

enum class RepeatMode {
    OFF,
    ONE,
    ALL
}

enum class ShuffleMode {
    OFF,
    ON
}

