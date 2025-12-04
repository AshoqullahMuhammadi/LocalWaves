package com.example.audioplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.audioplayer.data.model.Album
import com.example.audioplayer.data.model.Track
import com.example.audioplayer.ui.theme.GradientColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    album: Album?,
    tracks: List<Track>,
    currentTrack: Track?,
    onBackClick: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffleAll: () -> Unit,
    onTrackClick: (Track, List<Track>) -> Unit,
    onTrackMoreClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(GradientColors.darkBackground))
    ) {
        // Blurred background album art
        if (album?.artworkUri != null) {
            AsyncImage(
                model = album.artworkUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .blur(50.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF0D0D0D)
                            )
                        )
                    )
            )
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }
            
            // Album Header
            item {
                AlbumHeader(
                    album = album,
                    trackCount = tracks.size,
                    onPlayAll = onPlayAll,
                    onShuffleAll = onShuffleAll
                )
            }
            
            // Tracks
            items(
                items = tracks,
                key = { it.id }
            ) { track ->
                AlbumTrackItem(
                    track = track,
                    trackNumber = tracks.indexOf(track) + 1,
                    isPlaying = track.id == currentTrack?.id,
                    onClick = { onTrackClick(track, tracks) },
                    onMoreClick = { onTrackMoreClick(track) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable
private fun AlbumHeader(
    album: Album?,
    trackCount: Int,
    onPlayAll: () -> Unit,
    onShuffleAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Album Art
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2D2D44)),
            contentAlignment = Alignment.Center
        ) {
            if (album?.artworkUri != null) {
                AsyncImage(
                    model = album.artworkUri,
                    contentDescription = "Album art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Album Info
        Text(
            text = album?.title ?: "Unknown Album",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = album?.artist ?: "Unknown Artist",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
        
        Text(
            text = "$trackCount tracks" + if (album?.year != null && album.year > 0) " • ${album.year}" else "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onPlayAll,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Play All")
            }
            
            OutlinedButton(
                onClick = onShuffleAll,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Shuffle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Shuffle")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AlbumTrackItem(
    track: Track,
    trackNumber: Int,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track number
        Text(
            text = trackNumber.toString().padStart(2, '0'),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isPlaying) Color(0xFFE91E63) else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.width(32.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Track Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = if (isPlaying) Color(0xFFE91E63) else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.formattedDuration,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        
        // Playing indicator
        if (isPlaying) {
            Icon(
                Icons.Default.GraphicEq,
                contentDescription = "Playing",
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        IconButton(onClick = onMoreClick) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}
