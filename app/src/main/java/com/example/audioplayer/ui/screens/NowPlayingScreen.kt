package com.example.audioplayer.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.audioplayer.data.model.RepeatMode
import com.example.audioplayer.data.model.Track
import com.example.audioplayer.ui.components.*
import com.example.audioplayer.ui.theme.GradientColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    track: Track?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    repeatMode: RepeatMode,
    shuffleEnabled: Boolean,
    playbackSpeed: Float,
    queue: List<Track>,
    currentQueueIndex: Int,
    onBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onSeekStart: (Long) -> Unit,
    onSeekChange: (Long) -> Unit,
    onSeekEnd: (Long) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onQueueItemClick: (Int) -> Unit,
    onQueueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQueue by remember { mutableStateOf(false) }
    var showSpeedSelector by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        // Background with blurred album art
        if (track?.artworkUri != null) {
            AsyncImage(
                model = track.artworkUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(100.dp)
                    .scale(1.2f),
                contentScale = ContentScale.Crop
            )
        }
        
        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
        )
        
        if (showQueue) {
            QueueView(
                queue = queue,
                currentIndex = currentQueueIndex,
                onItemClick = onQueueItemClick,
                onBackClick = { showQueue = false },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Text(
                        text = "Now Playing",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    
                    IconButton(onClick = { showQueue = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "Queue",
                            tint = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(0.5f))
                
                // Album Art with animation
                val scale by animateFloatAsState(
                    targetValue = if (isPlaying) 1f else 0.9f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "albumArtScale"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Glow effect
                    if (track?.artworkUri != null) {
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .blur(40.dp)
                                .background(
                                    Color(0xFFE91E63).copy(alpha = 0.3f),
                                    RoundedCornerShape(32.dp)
                                )
                        )
                    }
                    
                    // Album art
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .scale(scale)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF2D2D44)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (track?.artworkUri != null) {
                            AsyncImage(
                                model = track.artworkUri,
                                contentDescription = "Album art",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Track Info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = track?.title ?: "No track playing",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = track?.artist ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Progress Bar
                ModernSeekBar(
                    currentPosition = currentPosition,
                    duration = duration,
                    onSeekStart = onSeekStart,
                    onSeekChange = onSeekChange,
                    onSeekEnd = onSeekEnd,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Playback Controls
                ModernPlaybackControls(
                    isPlaying = isPlaying,
                    repeatMode = repeatMode,
                    shuffleEnabled = shuffleEnabled,
                    onPlayPauseClick = onPlayPauseClick,
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick,
                    onRepeatClick = onRepeatClick,
                    onShuffleClick = onShuffleClick,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Speed Control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speed button
                    Surface(
                        onClick = { showSpeedSelector = !showSpeedSelector },
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${playbackSpeed}x",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                }
                
                // Speed Selector
                if (showSpeedSelector) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ModernSpeedSelector(
                        currentSpeed = playbackSpeed,
                        onSpeedSelected = { speed ->
                            onSpeedChange(speed)
                            showSpeedSelector = false
                        },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ModernSeekBar(
    currentPosition: Long,
    duration: Long,
    onSeekStart: (Long) -> Unit,
    onSeekChange: (Long) -> Unit,
    onSeekEnd: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableFloatStateOf(0f) }
    
    val progress = if (duration > 0) {
        if (isDragging) dragPosition else (currentPosition.toFloat() / duration)
    } else 0f
    
    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = progress.coerceIn(0f, 1f),
            onValueChange = { value ->
                isDragging = true
                dragPosition = value
                val newPosition = (value * duration).toLong()
                if (!isDragging) onSeekStart(newPosition)
                onSeekChange(newPosition)
            },
            onValueChangeFinished = {
                val finalPosition = (dragPosition * duration).toLong()
                onSeekEnd(finalPosition)
                isDragging = false
            },
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFE91E63),
                activeTrackColor = Color(0xFFE91E63),
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(if (isDragging) (dragPosition * duration).toLong() else currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ModernPlaybackControls(
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    shuffleEnabled: Boolean,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shuffle
        IconButton(
            onClick = onShuffleClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                tint = if (shuffleEnabled) Color(0xFFE91E63) else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Previous
        IconButton(
            onClick = onPreviousClick,
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        // Play/Pause
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFE91E63), CircleShape)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        // Next
        IconButton(
            onClick = onNextClick,
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        // Repeat
        IconButton(
            onClick = onRepeatClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = when (repeatMode) {
                    RepeatMode.ONE -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                },
                contentDescription = "Repeat",
                tint = if (repeatMode != RepeatMode.OFF) Color(0xFFE91E63) else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ModernSpeedSelector(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        speeds.forEach { speed ->
            val isSelected = currentSpeed == speed
            Surface(
                onClick = { onSpeedSelected(speed) },
                color = if (isSelected) Color(0xFFE91E63) else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "${speed}x",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QueueView(
    queue: List<Track>,
    currentIndex: Int,
    onItemClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(GradientColors.darkBackground)
            )
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Text(
                text = "Up Next",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            
            Text(
                text = "${queue.size} tracks",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            itemsIndexed(
                items = queue,
                key = { index, track -> "${track.id}_$index" }
            ) { index, track ->
                QueueTrackItem(
                    track = track,
                    isCurrentTrack = index == currentIndex,
                    queuePosition = index + 1,
                    onClick = { onItemClick(index) }
                )
            }
        }
    }
}

@Composable
private fun QueueTrackItem(
    track: Track,
    isCurrentTrack: Boolean,
    queuePosition: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isCurrentTrack) Color(0xFFE91E63).copy(alpha = 0.2f) else Color.Transparent
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = queuePosition.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isCurrentTrack) Color(0xFFE91E63) else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.width(32.dp)
        )
        
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2D2D44))
        ) {
            AsyncImage(
                model = track.artworkUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = if (isCurrentTrack) Color(0xFFE91E63) else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Text(
            text = track.formattedDuration,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f)
        )
        
        if (isCurrentTrack) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Playing",
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
