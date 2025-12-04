package com.example.audioplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.audioplayer.data.model.*
import com.example.audioplayer.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    tracks: List<Track>,
    albums: List<Album>,
    artists: List<Artist>,
    folders: List<Folder>,
    playlists: List<Playlist>,
    currentTrack: Track?,
    isScanning: Boolean,
    scanProgress: Pair<Int, Int>,
    onTrackClick: (Track, List<Track>) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onFolderClick: (Folder) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylist: () -> Unit,
    onScanMedia: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTrackMoreClick: (Track) -> Unit,
    onPlaylistMoreClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tracks", "Albums", "Artists", "Folders", "Playlists")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LocalWave") },
                actions = {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onScanMedia) {
                        Icon(Icons.Default.Refresh, contentDescription = "Scan media")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scanning progress
            if (isScanning && scanProgress.second > 0) {
                LinearProgressIndicator(
                    progress = { scanProgress.first.toFloat() / scanProgress.second },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Scanning: ${scanProgress.first} / ${scanProgress.second}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                text = when (index) {
                                    0 -> "$title (${tracks.size})"
                                    1 -> "$title (${albums.size})"
                                    2 -> "$title (${artists.size})"
                                    3 -> "$title (${folders.size})"
                                    4 -> "$title (${playlists.size})"
                                    else -> title
                                }
                            ) 
                        }
                    )
                }
            }
            
            // Content
            when (selectedTabIndex) {
                0 -> TracksTab(
                    tracks = tracks,
                    currentTrack = currentTrack,
                    onTrackClick = { track -> onTrackClick(track, tracks) },
                    onMoreClick = onTrackMoreClick
                )
                1 -> AlbumsTab(
                    albums = albums,
                    onAlbumClick = onAlbumClick
                )
                2 -> ArtistsTab(
                    artists = artists,
                    onArtistClick = onArtistClick
                )
                3 -> FoldersTab(
                    folders = folders,
                    onFolderClick = onFolderClick
                )
                4 -> PlaylistsTab(
                    playlists = playlists,
                    onPlaylistClick = onPlaylistClick,
                    onCreatePlaylist = onCreatePlaylist,
                    onMoreClick = onPlaylistMoreClick
                )
            }
        }
    }
}

@Composable
private fun TracksTab(
    tracks: List<Track>,
    currentTrack: Track?,
    onTrackClick: (Track) -> Unit,
    onMoreClick: (Track) -> Unit
) {
    if (tracks.isEmpty()) {
        EmptyContent(
            icon = Icons.Default.MusicNote,
            title = "No tracks found",
            subtitle = "Tap refresh to scan for audio files"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = tracks,
                key = { it.id }
            ) { track ->
                TrackListItem(
                    track = track,
                    isPlaying = track.id == currentTrack?.id,
                    onClick = { onTrackClick(track) },
                    onMoreClick = { onMoreClick(track) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp)) // Space for mini player
            }
        }
    }
}

@Composable
private fun AlbumsTab(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit
) {
    if (albums.isEmpty()) {
        EmptyContent(
            icon = Icons.Default.Album,
            title = "No albums found",
            subtitle = "Albums will appear after scanning"
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = albums,
                key = { it.id }
            ) { album ->
                AlbumGridItem(
                    album = album,
                    onClick = { onAlbumClick(album) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun ArtistsTab(
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit
) {
    if (artists.isEmpty()) {
        EmptyContent(
            icon = Icons.Default.Person,
            title = "No artists found",
            subtitle = "Artists will appear after scanning"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = artists,
                key = { it.id }
            ) { artist ->
                ArtistListItem(
                    artist = artist,
                    onClick = { onArtistClick(artist) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun FoldersTab(
    folders: List<Folder>,
    onFolderClick: (Folder) -> Unit
) {
    if (folders.isEmpty()) {
        EmptyContent(
            icon = Icons.Default.Folder,
            title = "No folders found",
            subtitle = "Folders will appear after scanning"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = folders,
                key = { it.path }
            ) { folder ->
                FolderListItem(
                    folder = folder,
                    onClick = { onFolderClick(folder) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun PlaylistsTab(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylist: () -> Unit,
    onMoreClick: (Playlist) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Create playlist button
        TextButton(
            onClick = onCreatePlaylist,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create new playlist")
        }
        
        if (playlists.isEmpty()) {
            EmptyContent(
                icon = Icons.Default.QueueMusic,
                title = "No playlists yet",
                subtitle = "Create a playlist to organize your music"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = playlists,
                    key = { it.id }
                ) { playlist ->
                    PlaylistListItem(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) },
                        onMoreClick = { onMoreClick(playlist) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

