package com.example.audioplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.model.Playlist
import com.example.audioplayer.data.model.Track
import com.example.audioplayer.data.repository.MediaRepository
import com.example.audioplayer.localWaveApp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    
    private val mediaRepository: MediaRepository = application.localWaveApp.mediaRepository
    
    val playlists: StateFlow<List<Playlist>> = mediaRepository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()
    
    private val _playlistTracks = MutableStateFlow<List<Track>>(emptyList())
    val playlistTracks: StateFlow<List<Track>> = _playlistTracks.asStateFlow()
    
    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()
    
    private val _showRenameDialog = MutableStateFlow(false)
    val showRenameDialog: StateFlow<Boolean> = _showRenameDialog.asStateFlow()
    
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()
    
    private val _showAddToPlaylistDialog = MutableStateFlow(false)
    val showAddToPlaylistDialog: StateFlow<Boolean> = _showAddToPlaylistDialog.asStateFlow()
    
    private val _trackToAdd = MutableStateFlow<Track?>(null)
    val trackToAdd: StateFlow<Track?> = _trackToAdd.asStateFlow()
    
    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            _selectedPlaylist.value = mediaRepository.getPlaylistById(playlistId)
            mediaRepository.getPlaylistTracks(playlistId).collect { tracks ->
                _playlistTracks.value = tracks
            }
        }
    }
    
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            mediaRepository.createPlaylist(name)
            _showCreateDialog.value = false
        }
    }
    
    fun renamePlaylist(playlistId: Long, newName: String) {
        viewModelScope.launch {
            mediaRepository.renamePlaylist(playlistId, newName)
            _showRenameDialog.value = false
            loadPlaylist(playlistId)
        }
    }
    
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            mediaRepository.deletePlaylist(playlistId)
            _showDeleteDialog.value = false
            _selectedPlaylist.value = null
        }
    }
    
    fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            mediaRepository.addTrackToPlaylist(playlistId, trackId)
            _showAddToPlaylistDialog.value = false
            _trackToAdd.value = null
        }
    }
    
    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            mediaRepository.removeTrackFromPlaylist(playlistId, trackId)
        }
    }
    
    fun showCreatePlaylistDialog() {
        _showCreateDialog.value = true
    }
    
    fun hideCreatePlaylistDialog() {
        _showCreateDialog.value = false
    }
    
    fun showRenamePlaylistDialog() {
        _showRenameDialog.value = true
    }
    
    fun hideRenamePlaylistDialog() {
        _showRenameDialog.value = false
    }
    
    fun showDeletePlaylistDialog() {
        _showDeleteDialog.value = true
    }
    
    fun hideDeletePlaylistDialog() {
        _showDeleteDialog.value = false
    }
    
    fun showAddToPlaylistDialog(track: Track) {
        _trackToAdd.value = track
        _showAddToPlaylistDialog.value = true
    }
    
    fun hideAddToPlaylistDialog() {
        _showAddToPlaylistDialog.value = false
        _trackToAdd.value = null
    }
}

