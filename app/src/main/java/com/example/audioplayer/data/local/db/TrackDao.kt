package com.example.audioplayer.data.local.db

import androidx.room.*
import com.example.audioplayer.data.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: Long): Track?
    
    @Query("SELECT * FROM tracks WHERE id IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<Track>
    
    @Query("SELECT * FROM tracks WHERE albumId = :albumId ORDER BY trackNumber ASC, title ASC")
    fun getTracksByAlbum(albumId: Long): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE artistId = :artistId ORDER BY album ASC, trackNumber ASC")
    fun getTracksByArtist(artistId: Long): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE filePath LIKE :folderPath || '%' ORDER BY title ASC")
    fun getTracksByFolder(folderPath: String): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchTracks(query: String): Flow<List<Track>>
    
    @Query("SELECT DISTINCT SUBSTR(filePath, 1, LENGTH(filePath) - LENGTH(SUBSTR(filePath, INSTR(filePath, '/') + 1))) as path FROM tracks")
    fun getAllFolderPaths(): Flow<List<String>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<Track>)
    
    @Update
    suspend fun updateTrack(track: Track)
    
    @Delete
    suspend fun deleteTrack(track: Track)
    
    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()
    
    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTrackCount(): Int
}

