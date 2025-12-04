package com.example.audioplayer.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.audioplayer.data.model.Playlist
import com.example.audioplayer.data.model.Track
import com.example.audioplayer.ui.components.*
import com.example.audioplayer.ui.screens.*
import com.example.audioplayer.ui.viewmodel.*
import kotlinx.coroutines.delay
import java.net.URLDecoder

@Composable
fun AppNavigation(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val libraryViewModel: LibraryViewModel = viewModel()
    val playerViewModel: PlayerViewModel = viewModel()
    val playlistViewModel: PlaylistViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    
    // Player state
    val currentTrack by playerViewModel.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by playerViewModel.displayPosition.collectAsStateWithLifecycle()
    val duration by playerViewModel.duration.collectAsStateWithLifecycle()
    val repeatMode by playerViewModel.repeatMode.collectAsStateWithLifecycle()
    val shuffleEnabled by playerViewModel.shuffleEnabled.collectAsStateWithLifecycle()
    val playbackSpeed by playerViewModel.playbackSpeed.collectAsStateWithLifecycle()
    val queue by playerViewModel.queue.collectAsStateWithLifecycle()
    val currentQueueIndex by playerViewModel.currentQueueIndex.collectAsStateWithLifecycle()
    
    // Library state
    val tracks by libraryViewModel.tracks.collectAsStateWithLifecycle()
    val albums by libraryViewModel.albums.collectAsStateWithLifecycle()
    val artists by libraryViewModel.artists.collectAsStateWithLifecycle()
    val folders by libraryViewModel.folders.collectAsStateWithLifecycle()
    val playlists by libraryViewModel.playlists.collectAsStateWithLifecycle()
    val isScanning by libraryViewModel.isScanning.collectAsStateWithLifecycle()
    val scanProgress by libraryViewModel.scanProgress.collectAsStateWithLifecycle()
    val searchQuery by libraryViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by libraryViewModel.searchResults.collectAsStateWithLifecycle()
    
    // Playlist dialog state
    val showCreateDialog by playlistViewModel.showCreateDialog.collectAsStateWithLifecycle()
    val showAddToPlaylistDialog by playlistViewModel.showAddToPlaylistDialog.collectAsStateWithLifecycle()
    val trackToAdd by playlistViewModel.trackToAdd.collectAsStateWithLifecycle()
    
    // Track options state
    var selectedTrack by remember { mutableStateOf<Track?>(null) }
    var showTrackOptions by remember { mutableStateOf(false) }
    var selectedPlaylistForOptions by remember { mutableStateOf<Playlist?>(null) }
    var showPlaylistOptions by remember { mutableStateOf(false) }
    
    // Current route check
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showMiniPlayer = currentRoute != Routes.NowPlaying.route && currentTrack != null
    
    // Auto scan on launch - with slight delay to ensure UI is ready
    LaunchedEffect(hasPermission) {
        if (hasPermission && tracks.isEmpty()) {
            delay(500) // Small delay to let UI settle
            libraryViewModel.scanMedia()
        }
    }
    
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Library.route
            ) {
                composable(Routes.Library.route) {
                    if (!hasPermission) {
                        PermissionScreen(onRequestPermission = onRequestPermission)
                    } else {
                        LibraryScreen(
                            tracks = tracks,
                            albums = albums,
                            artists = artists,
                            folders = folders,
                            playlists = playlists,
                            currentTrack = currentTrack,
                            isScanning = isScanning,
                            scanProgress = scanProgress,
                            onTrackClick = { track, trackList ->
                                playerViewModel.playTrackFromList(trackList, track)
                            },
                            onAlbumClick = { album ->
                                navController.navigate(Routes.AlbumDetail.createRoute(album.id))
                            },
                            onArtistClick = { artist ->
                                navController.navigate(Routes.ArtistDetail.createRoute(artist.id))
                            },
                            onFolderClick = { folder ->
                                navController.navigate(Routes.FolderDetail.createRoute(folder.path))
                            },
                            onPlaylistClick = { playlist ->
                                navController.navigate(Routes.PlaylistDetail.createRoute(playlist.id))
                            },
                            onCreatePlaylist = { playlistViewModel.showCreatePlaylistDialog() },
                            onScanMedia = { libraryViewModel.scanMedia() },
                            onSearchClick = { navController.navigate(Routes.Search.route) },
                            onSettingsClick = { navController.navigate(Routes.Settings.route) },
                            onTrackMoreClick = { track ->
                                selectedTrack = track
                                showTrackOptions = true
                            },
                            onPlaylistMoreClick = { playlist ->
                                selectedPlaylistForOptions = playlist
                                showPlaylistOptions = true
                            }
                        )
                    }
                }
                
                composable(Routes.NowPlaying.route) {
                    NowPlayingScreen(
                        track = currentTrack,
                        isPlaying = isPlaying,
                        currentPosition = currentPosition,
                        duration = duration,
                        repeatMode = repeatMode,
                        shuffleEnabled = shuffleEnabled,
                        playbackSpeed = playbackSpeed,
                        queue = queue,
                        currentQueueIndex = currentQueueIndex,
                        onBackClick = { navController.popBackStack() },
                        onPlayPauseClick = { playerViewModel.togglePlayPause() },
                        onPreviousClick = { playerViewModel.seekToPrevious() },
                        onNextClick = { playerViewModel.seekToNext() },
                        onRepeatClick = { playerViewModel.toggleRepeatMode() },
                        onShuffleClick = { playerViewModel.toggleShuffle() },
                        onSeekStart = { playerViewModel.onSeekStart(it) },
                        onSeekChange = { playerViewModel.onSeekChange(it) },
                        onSeekEnd = { playerViewModel.onSeekEnd(it) },
                        onSpeedChange = { playerViewModel.setPlaybackSpeed(it) },
                        onQueueItemClick = { index -> playerViewModel.skipToQueueItem(index) },
                        onQueueClick = { }
                    )
                }
                
                composable(
                    route = Routes.AlbumDetail.route,
                    arguments = listOf(navArgument("albumId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
                    val albumTracks by libraryViewModel.getTracksByAlbum(albumId).collectAsStateWithLifecycle(emptyList())
                    var album by remember { mutableStateOf<com.example.audioplayer.data.model.Album?>(null) }
                    
                    LaunchedEffect(albumId) {
                        album = libraryViewModel.getAlbumById(albumId)
                    }
                    
                    AlbumDetailScreen(
                        album = album,
                        tracks = albumTracks,
                        currentTrack = currentTrack,
                        onBackClick = { navController.popBackStack() },
                        onPlayAll = { playerViewModel.playTracks(albumTracks) },
                        onShuffleAll = {
                            playerViewModel.playTracks(albumTracks.shuffled())
                        },
                        onTrackClick = { track, trackList ->
                            playerViewModel.playTrackFromList(trackList, track)
                        },
                        onTrackMoreClick = { track ->
                            selectedTrack = track
                            showTrackOptions = true
                        }
                    )
                }
                
                composable(
                    route = Routes.ArtistDetail.route,
                    arguments = listOf(navArgument("artistId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val artistId = backStackEntry.arguments?.getLong("artistId") ?: return@composable
                    val artistTracks by libraryViewModel.getTracksByArtist(artistId).collectAsStateWithLifecycle(emptyList())
                    val artistAlbums by libraryViewModel.getAlbumsByArtist(artistId).collectAsStateWithLifecycle(emptyList())
                    var artist by remember { mutableStateOf<com.example.audioplayer.data.model.Artist?>(null) }
                    
                    LaunchedEffect(artistId) {
                        artist = libraryViewModel.getArtistById(artistId)
                    }
                    
                    ArtistDetailScreen(
                        artist = artist,
                        albums = artistAlbums,
                        tracks = artistTracks,
                        currentTrack = currentTrack,
                        onBackClick = { navController.popBackStack() },
                        onPlayAll = { playerViewModel.playTracks(artistTracks) },
                        onShuffleAll = { playerViewModel.playTracks(artistTracks.shuffled()) },
                        onAlbumClick = { album ->
                            navController.navigate(Routes.AlbumDetail.createRoute(album.id))
                        },
                        onTrackClick = { track, trackList ->
                            playerViewModel.playTrackFromList(trackList, track)
                        },
                        onTrackMoreClick = { track ->
                            selectedTrack = track
                            showTrackOptions = true
                        }
                    )
                }
                
                composable(
                    route = Routes.FolderDetail.route,
                    arguments = listOf(navArgument("folderPath") { type = NavType.StringType })
                ) { backStackEntry ->
                    val folderPath = backStackEntry.arguments?.getString("folderPath")?.let {
                        URLDecoder.decode(it, "UTF-8")
                    } ?: return@composable
                    val folderTracks by libraryViewModel.getTracksByFolder(folderPath).collectAsStateWithLifecycle(emptyList())
                    
                    FolderDetailScreen(
                        folderPath = folderPath,
                        tracks = folderTracks,
                        currentTrack = currentTrack,
                        onBackClick = { navController.popBackStack() },
                        onPlayAll = { playerViewModel.playTracks(folderTracks) },
                        onShuffleAll = { playerViewModel.playTracks(folderTracks.shuffled()) },
                        onTrackClick = { track, trackList ->
                            playerViewModel.playTrackFromList(trackList, track)
                        },
                        onTrackMoreClick = { track ->
                            selectedTrack = track
                            showTrackOptions = true
                        }
                    )
                }
                
                composable(
                    route = Routes.PlaylistDetail.route,
                    arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: return@composable
                    val selectedPlaylist by playlistViewModel.selectedPlaylist.collectAsStateWithLifecycle()
                    val playlistTracks by playlistViewModel.playlistTracks.collectAsStateWithLifecycle()
                    val showRenameDialog by playlistViewModel.showRenameDialog.collectAsStateWithLifecycle()
                    val showDeleteDialog by playlistViewModel.showDeleteDialog.collectAsStateWithLifecycle()
                    
                    LaunchedEffect(playlistId) {
                        playlistViewModel.loadPlaylist(playlistId)
                    }
                    
                    PlaylistDetailScreen(
                        playlist = selectedPlaylist,
                        tracks = playlistTracks,
                        currentTrack = currentTrack,
                        onBackClick = { navController.popBackStack() },
                        onPlayAll = { playerViewModel.playTracks(playlistTracks) },
                        onShuffleAll = { playerViewModel.playTracks(playlistTracks.shuffled()) },
                        onRenameClick = { playlistViewModel.showRenamePlaylistDialog() },
                        onDeleteClick = { playlistViewModel.showDeletePlaylistDialog() },
                        onTrackClick = { track, trackList ->
                            playerViewModel.playTrackFromList(trackList, track)
                        },
                        onRemoveTrack = { track ->
                            playlistViewModel.removeTrackFromPlaylist(playlistId, track.id)
                        },
                        onTrackMoreClick = { track ->
                            selectedTrack = track
                            showTrackOptions = true
                        }
                    )
                    
                    if (showRenameDialog) {
                        RenamePlaylistDialog(
                            currentName = selectedPlaylist?.name ?: "",
                            onDismiss = { playlistViewModel.hideRenamePlaylistDialog() },
                            onRename = { newName ->
                                playlistViewModel.renamePlaylist(playlistId, newName)
                            }
                        )
                    }
                    
                    if (showDeleteDialog) {
                        DeletePlaylistDialog(
                            playlistName = selectedPlaylist?.name ?: "",
                            onDismiss = { playlistViewModel.hideDeletePlaylistDialog() },
                            onDelete = {
                                playlistViewModel.deletePlaylist(playlistId)
                                navController.popBackStack()
                            }
                        )
                    }
                }
                
                composable(Routes.Search.route) {
                    SearchScreen(
                        query = searchQuery,
                        results = searchResults,
                        currentTrack = currentTrack,
                        onQueryChange = { libraryViewModel.setSearchQuery(it) },
                        onBackClick = { navController.popBackStack() },
                        onTrackClick = { track ->
                            playerViewModel.playTrack(track)
                        },
                        onAlbumClick = { album ->
                            navController.navigate(Routes.AlbumDetail.createRoute(album.id))
                        },
                        onArtistClick = { artist ->
                            navController.navigate(Routes.ArtistDetail.createRoute(artist.id))
                        },
                        onPlaylistClick = { playlist ->
                            navController.navigate(Routes.PlaylistDetail.createRoute(playlist.id))
                        },
                        onTrackMoreClick = { track ->
                            selectedTrack = track
                            showTrackOptions = true
                        }
                    )
                }
                
                composable(Routes.Settings.route) {
                    val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()
                    val gaplessPlayback by settingsViewModel.gaplessPlayback.collectAsStateWithLifecycle()
                    val crossfadeDuration by settingsViewModel.crossfadeDuration.collectAsStateWithLifecycle()
                    val defaultPlaybackSpeed by settingsViewModel.defaultPlaybackSpeed.collectAsStateWithLifecycle()
                    val pauseOnHeadsetDisconnect by settingsViewModel.pauseOnHeadsetDisconnect.collectAsStateWithLifecycle()
                    val resumeAfterCall by settingsViewModel.resumeAfterCall.collectAsStateWithLifecycle()
                    val autoScanOnStartup by settingsViewModel.autoScanOnStartup.collectAsStateWithLifecycle()
                    
                    SettingsScreen(
                        themeMode = themeMode,
                        gaplessPlayback = gaplessPlayback,
                        crossfadeDuration = crossfadeDuration,
                        defaultPlaybackSpeed = defaultPlaybackSpeed,
                        pauseOnHeadsetDisconnect = pauseOnHeadsetDisconnect,
                        resumeAfterCall = resumeAfterCall,
                        autoScanOnStartup = autoScanOnStartup,
                        onBackClick = { navController.popBackStack() },
                        onThemeChange = { settingsViewModel.setThemeMode(it) },
                        onGaplessPlaybackChange = { settingsViewModel.setGaplessPlayback(it) },
                        onCrossfadeDurationChange = { settingsViewModel.setCrossfadeDuration(it) },
                        onDefaultPlaybackSpeedChange = { settingsViewModel.setDefaultPlaybackSpeed(it) },
                        onPauseOnHeadsetDisconnectChange = { settingsViewModel.setPauseOnHeadsetDisconnect(it) },
                        onResumeAfterCallChange = { settingsViewModel.setResumeAfterCall(it) },
                        onAutoScanOnStartupChange = { settingsViewModel.setAutoScanOnStartup(it) },
                        onRescanLibrary = { libraryViewModel.scanMedia() }
                    )
                }
            }
            
            // Mini Player
            if (showMiniPlayer) {
                val progress = if (duration > 0) currentPosition.toFloat() / duration else 0f
                
                MiniPlayer(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    progress = progress,
                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                    onNextClick = { playerViewModel.seekToNext() },
                    onClick = { navController.navigate(Routes.NowPlaying.route) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
    
    // Track options menu
    if (showTrackOptions && selectedTrack != null) {
        TrackOptionsMenu(
            expanded = true,
            onDismiss = {
                showTrackOptions = false
                selectedTrack = null
            },
            onPlayNext = {
                selectedTrack?.let { playerViewModel.addToQueueNext(it) }
            },
            onAddToQueue = {
                selectedTrack?.let { playerViewModel.addToQueue(it) }
            },
            onAddToPlaylist = {
                selectedTrack?.let { playlistViewModel.showAddToPlaylistDialog(it) }
            },
            onGoToAlbum = {
                selectedTrack?.let { track ->
                    navController.navigate(Routes.AlbumDetail.createRoute(track.albumId))
                }
            },
            onGoToArtist = {
                selectedTrack?.let { track ->
                    navController.navigate(Routes.ArtistDetail.createRoute(track.artistId))
                }
            }
        )
    }
    
    // Playlist options menu
    if (showPlaylistOptions && selectedPlaylistForOptions != null) {
        PlaylistOptionsMenu(
            expanded = true,
            onDismiss = {
                showPlaylistOptions = false
                selectedPlaylistForOptions = null
            },
            onRename = {
                selectedPlaylistForOptions?.let { playlist ->
                    navController.navigate(Routes.PlaylistDetail.createRoute(playlist.id))
                    // Will show rename dialog in detail screen
                }
            },
            onDelete = {
                selectedPlaylistForOptions?.let { playlist ->
                    playlistViewModel.deletePlaylist(playlist.id)
                }
            }
        )
    }
    
    // Create playlist dialog
    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { playlistViewModel.hideCreatePlaylistDialog() },
            onCreate = { name -> playlistViewModel.createPlaylist(name) }
        )
    }
    
    // Add to playlist dialog
    if (showAddToPlaylistDialog && trackToAdd != null) {
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = { playlistViewModel.hideAddToPlaylistDialog() },
            onPlaylistSelected = { playlistId ->
                trackToAdd?.let { track ->
                    playlistViewModel.addTrackToPlaylist(playlistId, track.id)
                }
            },
            onCreateNew = {
                playlistViewModel.hideAddToPlaylistDialog()
                playlistViewModel.showCreatePlaylistDialog()
            }
        )
    }
}

