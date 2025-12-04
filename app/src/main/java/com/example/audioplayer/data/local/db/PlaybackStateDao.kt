package com.example.audioplayer.data.local.db

import androidx.room.*
import com.example.audioplayer.data.model.PlaybackState
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackStateDao {
    @Query("SELECT * FROM playback_state WHERE id = 1")
    fun getPlaybackState(): Flow<PlaybackState?>
    
    @Query("SELECT * FROM playback_state WHERE id = 1")
    suspend fun getPlaybackStateSync(): PlaybackState?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaybackState(state: PlaybackState)
    
    @Query("UPDATE playback_state SET currentTrackId = :trackId WHERE id = 1")
    suspend fun updateCurrentTrack(trackId: Long?)
    
    @Query("UPDATE playback_state SET currentPosition = :position WHERE id = 1")
    suspend fun updatePosition(position: Long)
    
    @Query("UPDATE playback_state SET repeatMode = :repeatMode WHERE id = 1")
    suspend fun updateRepeatMode(repeatMode: Int)
    
    @Query("UPDATE playback_state SET shuffleEnabled = :shuffleEnabled WHERE id = 1")
    suspend fun updateShuffleEnabled(shuffleEnabled: Boolean)
    
    @Query("UPDATE playback_state SET playbackSpeed = :speed WHERE id = 1")
    suspend fun updatePlaybackSpeed(speed: Float)
    
    @Transaction
    suspend fun ensurePlaybackStateExists() {
        val existing = getPlaybackStateSync()
        if (existing == null) {
            savePlaybackState(PlaybackState())
        }
    }
}

