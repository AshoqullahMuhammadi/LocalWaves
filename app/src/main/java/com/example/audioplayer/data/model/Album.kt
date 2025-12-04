package com.example.audioplayer.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val artistId: Long,
    val artworkUri: String? = null,
    val trackCount: Int = 0,
    val year: Int = 0
) {
    val albumArtUri: Uri?
        get() = artworkUri?.let { Uri.parse(it) }
}

