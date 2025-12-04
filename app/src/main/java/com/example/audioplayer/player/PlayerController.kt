package com.example.audioplayer.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.audioplayer.data.model.RepeatMode
import com.example.audioplayer.data.model.Track
import com.example.audioplayer.player.service.PlayerService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ExecutionException

class PlayerController(private val context: Context) {
    
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()
    
    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled.asStateFlow()
    
    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()
    
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()
    
    private val _currentQueueIndex = MutableStateFlow(0)
    val currentQueueIndex: StateFlow<Int> = _currentQueueIndex.asStateFlow()
    
    private var currentQueue: List<Track> = emptyList()
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    fun initialize() {
        if (controllerFuture != null) return // Already initializing or initialized
        
        try {
            val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            
            controllerFuture?.addListener({
                try {
                    // Use isDone check instead of blocking get()
                    val future = controllerFuture
                    if (future != null && future.isDone && !future.isCancelled) {
                        mediaController = future.get()
                        setupPlayerListener()
                        _isInitialized.value = true
                    }
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
        controllerFuture = null
        _isInitialized.value = false
    }
    
    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentTrackFromMediaItem(mediaItem)
                _currentQueueIndex.value = mediaController?.currentMediaItemIndex ?: 0
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        _duration.value = mediaController?.duration ?: 0L
                    }
                    Player.STATE_ENDED -> {
                        _isPlaying.value = false
                    }
                }
            }
            
            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = when (repeatMode) {
                    Player.REPEAT_MODE_OFF -> RepeatMode.OFF
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    else -> RepeatMode.OFF
                }
            }
            
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleEnabled.value = shuffleModeEnabled
            }
            
            override fun onPlaybackParametersChanged(playbackParameters: androidx.media3.common.PlaybackParameters) {
                _playbackSpeed.value = playbackParameters.speed
            }
        })
    }
    
    private fun updateCurrentTrackFromMediaItem(mediaItem: MediaItem?) {
        mediaItem?.let { item ->
            val trackId = item.mediaId.toLongOrNull()
            if (trackId != null) {
                currentQueue.find { it.id == trackId }?.let { track ->
                    _currentTrack.value = track
                }
            }
        }
    }
    
    fun updatePosition(position: Long) {
        _currentPosition.value = position
    }
    
    fun getCurrentPosition(): Long {
        return mediaController?.currentPosition ?: 0L
    }
    
    fun playTrack(track: Track) {
        val controller = mediaController ?: return
        val mediaItem = createMediaItem(track)
        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
        currentQueue = listOf(track)
        _queue.value = currentQueue
        _currentTrack.value = track
        _currentQueueIndex.value = 0
    }
    
    fun playTracks(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        val controller = mediaController ?: return
        
        val mediaItems = tracks.map { createMediaItem(it) }
        controller.setMediaItems(mediaItems, startIndex, 0)
        controller.prepare()
        controller.play()
        currentQueue = tracks
        _queue.value = currentQueue
        _currentTrack.value = tracks.getOrNull(startIndex)
        _currentQueueIndex.value = startIndex
    }
    
    fun addToQueue(track: Track) {
        val controller = mediaController ?: return
        val mediaItem = createMediaItem(track)
        controller.addMediaItem(mediaItem)
        currentQueue = currentQueue + track
        _queue.value = currentQueue
    }
    
    fun addToQueueNext(track: Track) {
        val controller = mediaController ?: return
        val mediaItem = createMediaItem(track)
        val nextIndex = (controller.currentMediaItemIndex) + 1
        controller.addMediaItem(nextIndex, mediaItem)
        currentQueue = currentQueue.toMutableList().apply {
            add(nextIndex.coerceAtMost(size), track)
        }
        _queue.value = currentQueue
    }
    
    fun removeFromQueue(index: Int) {
        val controller = mediaController ?: return
        if (index >= 0 && index < currentQueue.size) {
            controller.removeMediaItem(index)
            currentQueue = currentQueue.toMutableList().apply {
                removeAt(index)
            }
            _queue.value = currentQueue
        }
    }
    
    fun clearQueue() {
        mediaController?.clearMediaItems()
        currentQueue = emptyList()
        _queue.value = currentQueue
        _currentTrack.value = null
    }
    
    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        val controller = mediaController ?: return
        controller.moveMediaItem(fromIndex, toIndex)
        currentQueue = currentQueue.toMutableList().apply {
            val item = removeAt(fromIndex)
            add(toIndex, item)
        }
        _queue.value = currentQueue
    }
    
    fun play() {
        mediaController?.play()
    }
    
    fun pause() {
        mediaController?.pause()
    }
    
    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }
    
    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
        _currentPosition.value = positionMs
    }
    
    fun seekToNext() {
        mediaController?.seekToNext()
    }
    
    fun seekToPrevious() {
        mediaController?.seekToPrevious()
    }
    
    fun skipToQueueItem(index: Int) {
        mediaController?.seekTo(index, 0)
    }
    
    fun setRepeatMode(mode: RepeatMode) {
        val controller = mediaController ?: return
        val playerRepeatMode = when (mode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
        controller.repeatMode = playerRepeatMode
        _repeatMode.value = mode
    }
    
    fun toggleRepeatMode() {
        val newMode = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        setRepeatMode(newMode)
    }
    
    fun setShuffleEnabled(enabled: Boolean) {
        val controller = mediaController ?: return
        controller.shuffleModeEnabled = enabled
        _shuffleEnabled.value = enabled
    }
    
    fun toggleShuffle() {
        setShuffleEnabled(!_shuffleEnabled.value)
    }
    
    fun setPlaybackSpeed(speed: Float) {
        mediaController?.setPlaybackSpeed(speed)
        _playbackSpeed.value = speed
    }
    
    fun stop() {
        mediaController?.stop()
        _isPlaying.value = false
    }
    
    private fun createMediaItem(track: Track): MediaItem {
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbumTitle(track.album)
            .setArtworkUri(track.albumArtUri)
            .build()
        
        return MediaItem.Builder()
            .setMediaId(track.id.toString())
            .setUri(track.contentUri)
            .setMediaMetadata(mediaMetadata)
            .setRequestMetadata(
                MediaItem.RequestMetadata.Builder()
                    .setMediaUri(track.contentUri)
                    .build()
            )
            .build()
    }
    
    fun restoreState(trackId: Long?, position: Long, repeatMode: RepeatMode, shuffleEnabled: Boolean, speed: Float, tracks: List<Track>) {
        val controller = mediaController ?: return
        if (tracks.isNotEmpty()) {
            currentQueue = tracks
            _queue.value = currentQueue
            
            val startIndex = tracks.indexOfFirst { it.id == trackId }.takeIf { it >= 0 } ?: 0
            val mediaItems = tracks.map { createMediaItem(it) }
            
            controller.setMediaItems(mediaItems, startIndex, position)
            controller.repeatMode = when (repeatMode) {
                RepeatMode.OFF -> Player.REPEAT_MODE_OFF
                RepeatMode.ONE -> Player.REPEAT_MODE_ONE
                RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            }
            controller.shuffleModeEnabled = shuffleEnabled
            controller.setPlaybackSpeed(speed)
            controller.prepare()
            
            _currentTrack.value = tracks.getOrNull(startIndex)
            _currentQueueIndex.value = startIndex
            _repeatMode.value = repeatMode
            _shuffleEnabled.value = shuffleEnabled
            _playbackSpeed.value = speed
            _currentPosition.value = position
        }
    }
}
