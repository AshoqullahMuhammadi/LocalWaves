package com.example.audioplayer.ui.navigation

sealed class Routes(val route: String) {
    data object Library : Routes("library")
    data object NowPlaying : Routes("now_playing")
    data object Playlists : Routes("playlists")
    data object PlaylistDetail : Routes("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }
    data object AlbumDetail : Routes("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }
    data object ArtistDetail : Routes("artist/{artistId}") {
        fun createRoute(artistId: Long) = "artist/$artistId"
    }
    data object FolderDetail : Routes("folder/{folderPath}") {
        fun createRoute(folderPath: String) = "folder/${java.net.URLEncoder.encode(folderPath, "UTF-8")}"
    }
    data object Settings : Routes("settings")
    data object Search : Routes("search")
    data object Queue : Routes("queue")
}

