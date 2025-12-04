package com.example.audioplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.model.RepeatMode
import com.example.audioplayer.data.model.Track
import com.example.audioplayer.data.repository.MediaRepository
import com.example.audioplayer.localWaveApp
import com.example.audioplayer.player.PlayerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val playerController: PlayerController = application.localWaveApp.playerController
    private val mediaRepository: MediaRepository = application.localWaveApp.mediaRepository
    
    val currentTrack: StateFlow<Track?> = playerController.currentTrack
    val isPlaying: StateFlow<Boolean> = playerController.isPlaying
    val currentPosition: StateFlow<Long> = playerController.currentPosition
    val duration: StateFlow<Long> = playerController.duration
    val repeatMode: StateFlow<RepeatMode> = playerController.repeatMode
    val shuffleEnabled: StateFlow<Boolean> = playerController.shuffleEnabled
    val playbackSpeed: StateFlow<Float> = playerController.playbackSpeed
    val queue: StateFlow<List<Track>> = playerController.queue
    val currentQueueIndex: StateFlow<Int> = playerController.currentQueueIndex
    
    private var positionUpdateJob: Job? = null
    
    private val _isSeeking = MutableStateFlow(false)
    val isSeeking: StateFlow<Boolean> = _isSeeking.asStateFlow()
    
    private val _seekPosition = MutableStateFlow(0L)
    val seekPosition: StateFlow<Long> = _seekPosition.asStateFlow()
    
    val displayPosition: StateFlow<Long> = combine(isSeeking, seekPosition, currentPosition) { seeking, seekPos, currentPos ->
        if (seeking) seekPos else currentPos
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)
    
    init {
        // Initialize player controller (non-blocking)
        playerController.initialize()
        
        // Start position updates
        startPositionUpdates()
        
        // Restore playback state when controller is ready
        viewModelScope.launch {
            playerController.isInitialized.collect { initialized ->
                if (initialized) {
                    restorePlaybackState()
                }
            }
        }
    }
    
    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (isActive) {
                if (isPlaying.value && !_isSeeking.value) {
                    playerController.updatePosition(playerController.getCurrentPosition())
                }
                delay(200)
            }
        }
    }
    
    private fun restorePlaybackState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mediaRepository.ensurePlaybackStateExists()
                
                val state = mediaRepository.getPlaybackStateSync() ?: return@launch
                val queueTracks = mediaRepository.getQueueTracks().first()
                
                if (queueTracks.isNotEmpty() && state.currentTrackId != null) {
                    withContext(Dispatchers.Main) {
                        playerController.restoreState(
                            trackId = state.currentTrackId,
                            position = state.currentPosition,
                            repeatMode = RepeatMode.entries[state.repeatMode],
                            shuffleEnabled = state.shuffleEnabled,
                            speed = state.playbackSpeed,
                            tracks = queueTracks
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun playTrack(track: Track) {
        playerController.playTrack(track)
        saveCurrentTrackToQueue(listOf(track))
    }
    
    fun playTracks(tracks: List<Track>, startIndex: Int = 0) {
        playerController.playTracks(tracks, startIndex)
        saveCurrentTrackToQueue(tracks)
    }
    
    fun playTrackFromList(tracks: List<Track>, track: Track) {
        val index = tracks.indexOf(track).takeIf { it >= 0 } ?: 0
        playTracks(tracks, index)
    }
    
    private fun saveCurrentTrackToQueue(tracks: List<Track>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mediaRepository.replaceQueue(tracks.map { it.id })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun togglePlayPause() {
        playerController.togglePlayPause()
    }
    
    fun play() {
        playerController.play()
    }
    
    fun pause() {
        playerController.pause()
    }
    
    fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
    }
    
    fun onSeekStart(position: Long) {
        _isSeeking.value = true
        _seekPosition.value = position
    }
    
    fun onSeekChange(position: Long) {
        _seekPosition.value = position
    }
    
    fun onSeekEnd(position: Long) {
        _isSeeking.value = false
        seekTo(position)
    }
    
    fun seekToNext() {
        playerController.seekToNext()
    }
    
    fun seekToPrevious() {
        playerController.seekToPrevious()
    }
    
    fun skipToQueueItem(index: Int) {
        playerController.skipToQueueItem(index)
    }
    
    fun toggleRepeatMode() {
        playerController.toggleRepeatMode()
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.updateRepeatMode(repeatMode.value.ordinal)
        }
    }
    
    fun toggleShuffle() {
        playerController.toggleShuffle()
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.updateShuffleEnabled(shuffleEnabled.value)
        }
    }
    
    fun setPlaybackSpeed(speed: Float) {
        playerController.setPlaybackSpeed(speed)
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.updatePlaybackSpeed(speed)
        }
    }
    
    fun addToQueue(track: Track) {
        playerController.addToQueue(track)
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.addToQueue(track.id)
        }
    }
    
    fun addToQueueNext(track: Track) {
        playerController.addToQueueNext(track)
        viewModelScope.launch(Dispatchers.IO) {
            val currentIndex = currentQueueIndex.value
            mediaRepository.addToQueueNext(track.id, currentIndex)
        }
    }
    
    fun removeFromQueue(index: Int) {
        val track = queue.value.getOrNull(index) ?: return
        playerController.removeFromQueue(index)
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.removeFromQueue(track.id)
        }
    }
    
    fun clearQueue() {
        playerController.clearQueue()
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.clearQueue()
        }
    }
    
    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        playerController.moveQueueItem(fromIndex, toIndex)
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.moveQueueItem(fromIndex, toIndex)
        }
    }
    
    fun savePlaybackState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentTrack.value?.let { track ->
                    mediaRepository.updateCurrentTrack(track.id)
                    mediaRepository.updatePosition(currentPosition.value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        positionUpdateJob?.cancel()
        savePlaybackState()
        // Don't release player controller here - it's managed by the Application
    }
}
