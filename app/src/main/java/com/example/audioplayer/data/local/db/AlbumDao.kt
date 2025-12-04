package com.example.audioplayer.data.local.db

import androidx.room.*
import com.example.audioplayer.data.model.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums ORDER BY title ASC")
    fun getAllAlbums(): Flow<List<Album>>
    
    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumById(albumId: Long): Album?
    
    @Query("SELECT * FROM albums WHERE artistId = :artistId ORDER BY year DESC, title ASC")
    fun getAlbumsByArtist(artistId: Long): Flow<List<Album>>
    
    @Query("SELECT * FROM albums WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchAlbums(query: String): Flow<List<Album>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: Album)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<Album>)
    
    @Update
    suspend fun updateAlbum(album: Album)
    
    @Delete
    suspend fun deleteAlbum(album: Album)
    
    @Query("DELETE FROM albums")
    suspend fun deleteAllAlbums()
}

