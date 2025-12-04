package com.example.audioplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.audioplayer.data.local.db.*
import com.example.audioplayer.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MediaRepository(
    private val context: Context,
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val playlistDao: PlaylistDao,
    private val queueDao: QueueDao,
    private val playbackStateDao: PlaybackStateDao
) {
    // Tracks
    fun getAllTracks(): Flow<List<Track>> = trackDao.getAllTracks()
    
    fun getTracksByAlbum(albumId: Long): Flow<List<Track>> = trackDao.getTracksByAlbum(albumId)
    
    fun getTracksByArtist(artistId: Long): Flow<List<Track>> = trackDao.getTracksByArtist(artistId)
    
    fun getTracksByFolder(folderPath: String): Flow<List<Track>> = trackDao.getTracksByFolder(folderPath)
    
    fun searchTracks(query: String): Flow<List<Track>> = trackDao.searchTracks(query)
    
    suspend fun getTrackById(trackId: Long): Track? = trackDao.getTrackById(trackId)
    
    suspend fun getTracksByIds(trackIds: List<Long>): List<Track> = trackDao.getTracksByIds(trackIds)
    
    // Albums
    fun getAllAlbums(): Flow<List<Album>> = albumDao.getAllAlbums()
    
    fun getAlbumsByArtist(artistId: Long): Flow<List<Album>> = albumDao.getAlbumsByArtist(artistId)
    
    fun searchAlbums(query: String): Flow<List<Album>> = albumDao.searchAlbums(query)
    
    suspend fun getAlbumById(albumId: Long): Album? = albumDao.getAlbumById(albumId)
    
    // Artists
    fun getAllArtists(): Flow<List<Artist>> = artistDao.getAllArtists()
    
    fun searchArtists(query: String): Flow<List<Artist>> = artistDao.searchArtists(query)
    
    suspend fun getArtistById(artistId: Long): Artist? = artistDao.getArtistById(artistId)
    
    // Folders
    fun getAllFolders(): Flow<List<Folder>> = trackDao.getAllTracks().map { tracks ->
        tracks.groupBy { track ->
            track.filePath.substringBeforeLast("/")
        }.map { (path, tracksInFolder) ->
            Folder(
                path = path,
                name = path.substringAfterLast("/"),
                trackCount = tracksInFolder.size
            )
        }.sortedBy { it.name.lowercase() }
    }
    
    // Playlists
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>> = playlistDao.getPlaylistTracks(playlistId)
    
    fun searchPlaylists(query: String): Flow<List<Playlist>> = playlistDao.searchPlaylists(query)
    
    suspend fun getPlaylistById(playlistId: Long): Playlist? = playlistDao.getPlaylistById(playlistId)
    
    suspend fun createPlaylist(name: String): Long = 
        playlistDao.insertPlaylist(Playlist(name = name))
    
    suspend fun renamePlaylist(playlistId: Long, newName: String) {
        playlistDao.getPlaylistById(playlistId)?.let { playlist ->
            playlistDao.updatePlaylist(playlist.copy(name = newName, updatedAt = System.currentTimeMillis()))
        }
    }
    
    suspend fun deletePlaylist(playlistId: Long) = playlistDao.deletePlaylistById(playlistId)
    
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) = 
        playlistDao.addTrackToPlaylist(playlistId, trackId)
    
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) = 
        playlistDao.removeTrackAndUpdateCount(playlistId, trackId)
    
    // Queue
    fun getQueueTracks(): Flow<List<Track>> = queueDao.getQueueTracks()
    
    suspend fun addToQueue(trackId: Long) = queueDao.addToQueue(trackId)
    
    suspend fun addToQueueNext(trackId: Long, afterPosition: Int) = queueDao.addToQueueNext(trackId, afterPosition)
    
    suspend fun removeFromQueue(trackId: Long) = queueDao.removeTrackFromQueue(trackId)
    
    suspend fun clearQueue() = queueDao.clearQueue()
    
    suspend fun replaceQueue(trackIds: List<Long>) = queueDao.replaceQueue(trackIds)
    
    suspend fun moveQueueItem(fromPosition: Int, toPosition: Int) = queueDao.moveItem(fromPosition, toPosition)
    
    // Playback State
    fun getPlaybackState(): Flow<PlaybackState?> = playbackStateDao.getPlaybackState()
    
    suspend fun getPlaybackStateSync(): PlaybackState? = playbackStateDao.getPlaybackStateSync()
    
    suspend fun savePlaybackState(state: PlaybackState) = playbackStateDao.savePlaybackState(state)
    
    suspend fun updateCurrentTrack(trackId: Long?) = playbackStateDao.updateCurrentTrack(trackId)
    
    suspend fun updatePosition(position: Long) = playbackStateDao.updatePosition(position)
    
    suspend fun updateRepeatMode(repeatMode: Int) = playbackStateDao.updateRepeatMode(repeatMode)
    
    suspend fun updateShuffleEnabled(shuffleEnabled: Boolean) = playbackStateDao.updateShuffleEnabled(shuffleEnabled)
    
    suspend fun updatePlaybackSpeed(speed: Float) = playbackStateDao.updatePlaybackSpeed(speed)
    
    suspend fun ensurePlaybackStateExists() = playbackStateDao.ensurePlaybackStateExists()
    
    // Media Scanning
    suspend fun scanMedia(onProgress: (Int, Int) -> Unit = { _, _ -> }): Int = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()
        val albums = mutableMapOf<Long, Album>()
        val artists = mutableMapOf<Long, Artist>()
        
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        
        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            
            val totalCount = cursor.count
            var processedCount = 0
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val artistId = cursor.getLong(artistIdColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val artistName = cursor.getStringOrDefault(artistColumn, "Unknown Artist")
                val albumName = cursor.getStringOrDefault(albumColumn, "Unknown Album")
                val year = cursor.getIntOrDefault(yearColumn, 0)
                
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                
                val track = Track(
                    id = id,
                    uri = contentUri.toString(),
                    title = cursor.getStringOrDefault(titleColumn, "Unknown"),
                    artist = artistName,
                    album = albumName,
                    albumId = albumId,
                    artistId = artistId,
                    durationMs = cursor.getLong(durationColumn),
                    filePath = cursor.getStringOrDefault(dataColumn, ""),
                    mimeType = cursor.getStringOrDefault(mimeTypeColumn, "audio/*"),
                    size = cursor.getLong(sizeColumn),
                    trackNumber = cursor.getIntOrDefault(trackColumn, 0),
                    year = year,
                    dateAdded = cursor.getLong(dateAddedColumn) * 1000,
                    dateModified = cursor.getLong(dateModifiedColumn) * 1000,
                    artworkUri = albumArtUri.toString()
                )
                
                tracks.add(track)
                
                // Collect album info
                if (!albums.containsKey(albumId)) {
                    albums[albumId] = Album(
                        id = albumId,
                        title = albumName,
                        artist = artistName,
                        artistId = artistId,
                        artworkUri = albumArtUri.toString(),
                        year = year
                    )
                }
                
                // Collect artist info
                if (!artists.containsKey(artistId)) {
                    artists[artistId] = Artist(
                        id = artistId,
                        name = artistName
                    )
                }
                
                processedCount++
                onProgress(processedCount, totalCount)
            }
        }
        
        // Calculate counts
        val albumsWithCounts = albums.values.map { album ->
            album.copy(trackCount = tracks.count { it.albumId == album.id })
        }
        
        val artistsWithCounts = artists.values.map { artist ->
            artist.copy(
                trackCount = tracks.count { it.artistId == artist.id },
                albumCount = albumsWithCounts.count { it.artistId == artist.id }
            )
        }
        
        // Clear and insert
        trackDao.deleteAllTracks()
        albumDao.deleteAllAlbums()
        artistDao.deleteAllArtists()
        
        trackDao.insertTracks(tracks)
        albumDao.insertAlbums(albumsWithCounts)
        artistDao.insertArtists(artistsWithCounts)
        
        tracks.size
    }
    
    private fun Cursor.getStringOrDefault(columnIndex: Int, default: String): String {
        return getString(columnIndex) ?: default
    }
    
    private fun Cursor.getIntOrDefault(columnIndex: Int, default: Int): Int {
        return try {
            getInt(columnIndex)
        } catch (e: Exception) {
            default
        }
    }
}

