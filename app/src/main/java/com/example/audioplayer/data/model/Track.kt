package com.example.audioplayer.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey
    val id: Long,
    val uri: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val artistId: Long,
    val durationMs: Long,
    val filePath: String,
    val mimeType: String,
    val size: Long,
    val bitrate: Int = 0,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val genre: String = "",
    val dateAdded: Long,
    val dateModified: Long,
    val artworkUri: String? = null
) {
    val contentUri: Uri
        get() = Uri.parse(uri)
    
    val albumArtUri: Uri?
        get() = artworkUri?.let { Uri.parse(it) }
    
    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

