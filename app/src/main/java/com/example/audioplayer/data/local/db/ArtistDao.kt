package com.example.audioplayer.data.local.db

import androidx.room.*
import com.example.audioplayer.data.model.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artists ORDER BY name ASC")
    fun getAllArtists(): Flow<List<Artist>>
    
    @Query("SELECT * FROM artists WHERE id = :artistId")
    suspend fun getArtistById(artistId: Long): Artist?
    
    @Query("SELECT * FROM artists WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchArtists(query: String): Flow<List<Artist>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: Artist)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<Artist>)
    
    @Update
    suspend fun updateArtist(artist: Artist)
    
    @Delete
    suspend fun deleteArtist(artist: Artist)
    
    @Query("DELETE FROM artists")
    suspend fun deleteAllArtists()
}

