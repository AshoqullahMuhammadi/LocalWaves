package com.example.audioplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.model.*
import com.example.audioplayer.data.repository.MediaRepository
import com.example.audioplayer.localWaveApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val mediaRepository: MediaRepository = application.localWaveApp.mediaRepository
    
    val tracks: StateFlow<List<Track>> = mediaRepository.getAllTracks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val albums: StateFlow<List<Album>> = mediaRepository.getAllAlbums()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val artists: StateFlow<List<Artist>> = mediaRepository.getAllArtists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val folders: StateFlow<List<Folder>> = mediaRepository.getAllFolders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val playlists: StateFlow<List<Playlist>> = mediaRepository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val _scanProgress = MutableStateFlow(Pair(0, 0))
    val scanProgress: StateFlow<Pair<Int, Int>> = _scanProgress.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val searchResults: StateFlow<SearchResults> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(SearchResults())
            } else {
                combine(
                    mediaRepository.searchTracks(query),
                    mediaRepository.searchAlbums(query),
                    mediaRepository.searchArtists(query),
                    mediaRepository.searchPlaylists(query)
                ) { tracks, albums, artists, playlists ->
                    SearchResults(tracks, albums, artists, playlists)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, SearchResults())
    
    fun scanMedia() {
        if (_isScanning.value) return // Prevent multiple scans
        
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            _scanProgress.value = Pair(0, 0)
            
            try {
                var lastUpdateTime = 0L
                mediaRepository.scanMedia { processed, total ->
                    // Throttle progress updates to avoid too many recompositions
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastUpdateTime > 100 || processed == total) {
                        lastUpdateTime = currentTime
                        _scanProgress.value = Pair(processed, total)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                withContext(Dispatchers.Main) {
                    _isScanning.value = false
                }
            }
        }
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun getTracksByAlbum(albumId: Long): Flow<List<Track>> = 
        mediaRepository.getTracksByAlbum(albumId)
    
    fun getTracksByArtist(artistId: Long): Flow<List<Track>> = 
        mediaRepository.getTracksByArtist(artistId)
    
    fun getTracksByFolder(folderPath: String): Flow<List<Track>> = 
        mediaRepository.getTracksByFolder(folderPath)
    
    fun getAlbumsByArtist(artistId: Long): Flow<List<Album>> = 
        mediaRepository.getAlbumsByArtist(artistId)
    
    suspend fun getAlbumById(albumId: Long): Album? = withContext(Dispatchers.IO) {
        mediaRepository.getAlbumById(albumId)
    }
    
    suspend fun getArtistById(artistId: Long): Artist? = withContext(Dispatchers.IO) {
        mediaRepository.getArtistById(artistId)
    }
}

data class SearchResults(
    val tracks: List<Track> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<Playlist> = emptyList()
) {
    val isEmpty: Boolean
        get() = tracks.isEmpty() && albums.isEmpty() && artists.isEmpty() && playlists.isEmpty()
    
    val totalCount: Int
        get() = tracks.size + albums.size + artists.size + playlists.size
}
