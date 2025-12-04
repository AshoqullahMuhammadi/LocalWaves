package com.example.audioplayer.data.model

data class Folder(
    val path: String,
    val name: String,
    val trackCount: Int = 0
) {
    val displayName: String
        get() = name.ifEmpty { path.substringAfterLast("/") }
}

