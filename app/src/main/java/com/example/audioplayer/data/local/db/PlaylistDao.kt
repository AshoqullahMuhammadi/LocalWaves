package com.example.audioplayer.data.local.db

import androidx.room.*
import com.example.audioplayer.data.model.Playlist
import com.example.audioplayer.data.model.PlaylistItem
import com.example.audioplayer.data.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>
    
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): Playlist?
    
    @Query("SELECT * FROM playlists WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchPlaylists(query: String): Flow<List<Playlist>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long
    
    @Update
    suspend fun updatePlaylist(playlist: Playlist)
    
    @Delete
    suspend fun deletePlaylist(playlist: Playlist)
    
    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)
    
    // Playlist Items
    @Query("""
        SELECT t.* FROM tracks t 
        INNER JOIN playlist_items pi ON t.id = pi.trackId 
        WHERE pi.playlistId = :playlistId 
        ORDER BY pi.position ASC
    """)
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>
    
    @Query("SELECT * FROM playlist_items WHERE playlistId = :playlistId ORDER BY position ASC")
    fun getPlaylistItems(playlistId: Long): Flow<List<PlaylistItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistItem(item: PlaylistItem)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistItems(items: List<PlaylistItem>)
    
    @Delete
    suspend fun deletePlaylistItem(item: PlaylistItem)
    
    @Query("DELETE FROM playlist_items WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    
    @Query("DELETE FROM playlist_items WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
    
    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM playlist_items WHERE playlistId = :playlistId")
    suspend fun getNextPosition(playlistId: Long): Int
    
    @Query("UPDATE playlists SET trackCount = (SELECT COUNT(*) FROM playlist_items WHERE playlistId = :playlistId), updatedAt = :updatedAt WHERE id = :playlistId")
    suspend fun updatePlaylistTrackCount(playlistId: Long, updatedAt: Long = System.currentTimeMillis())
    
    @Transaction
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val position = getNextPosition(playlistId)
        insertPlaylistItem(PlaylistItem(playlistId = playlistId, trackId = trackId, position = position))
        updatePlaylistTrackCount(playlistId)
    }
    
    @Transaction
    suspend fun removeTrackAndUpdateCount(playlistId: Long, trackId: Long) {
        removeTrackFromPlaylist(playlistId, trackId)
        updatePlaylistTrackCount(playlistId)
    }
}

