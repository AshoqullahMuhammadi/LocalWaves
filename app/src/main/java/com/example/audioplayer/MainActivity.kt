package com.example.audioplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audioplayer.data.local.preferences.ThemeMode
import com.example.audioplayer.data.model.Track
import com.example.audioplayer.ui.navigation.AppNavigation
import com.example.audioplayer.ui.theme.AudioPlayerTheme

class MainActivity : ComponentActivity() {
    
    private var hasPermission by mutableStateOf(false)
    private var externalFileUri by mutableStateOf<Uri?>(null)
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions.values.all { it }
        // After permission granted, process external file if pending
        if (hasPermission && externalFileUri != null) {
            processExternalFile(externalFileUri!!)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Check current permission status
        hasPermission = checkMediaPermission()
        
        // Handle intent from file manager
        handleIntent(intent)
        
        setContent {
            val userPreferences = (application as LocalWaveApplication).userPreferences
            val themeMode by userPreferences.themeMode.collectAsStateWithLifecycle(ThemeMode.SYSTEM)
            
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            
            AudioPlayerTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        hasPermission = hasPermission,
                        onRequestPermission = { requestMediaPermission() },
                        externalTrack = remember { mutableStateOf(externalFileUri) }.value?.let { uri ->
                            createTrackFromUri(uri)
                        }
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { uri ->
                externalFileUri = uri
                if (hasPermission) {
                    processExternalFile(uri)
                } else {
                    requestMediaPermission()
                }
            }
        }
    }
    
    private fun processExternalFile(uri: Uri) {
        // The track will be created and played in AppNavigation
        externalFileUri = uri
    }
    
    private fun createTrackFromUri(uri: Uri): Track? {
        return try {
            val cursor = contentResolver.query(
                uri,
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID
                ),
                null,
                null,
                null
            )
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)) 
                        ?: uri.lastPathSegment ?: "Unknown"
                    val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) 
                        ?: "Unknown Artist"
                    val album = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)) 
                        ?: "Unknown Album"
                    val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    val albumId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    
                    val albumArtUri = android.content.ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )
                    
                    Track(
                        id = id,
                        uri = uri.toString(),
                        title = title,
                        artist = artist,
                        album = album,
                        albumId = albumId,
                        artistId = 0,
                        durationMs = duration,
                        filePath = uri.path ?: "",
                        mimeType = contentResolver.getType(uri) ?: "audio/*",
                        size = 0,
                        dateAdded = System.currentTimeMillis(),
                        dateModified = System.currentTimeMillis(),
                        artworkUri = albumArtUri.toString()
                    )
                } else {
                    // Create basic track from URI
                    createBasicTrackFromUri(uri)
                }
            } ?: createBasicTrackFromUri(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            createBasicTrackFromUri(uri)
        }
    }
    
    private fun createBasicTrackFromUri(uri: Uri): Track {
        val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "Unknown"
        val title = fileName.substringBeforeLast(".")
        
        return Track(
            id = uri.hashCode().toLong(),
            uri = uri.toString(),
            title = title,
            artist = "Unknown Artist",
            album = "Unknown Album",
            albumId = 0,
            artistId = 0,
            durationMs = 0,
            filePath = uri.path ?: "",
            mimeType = contentResolver.getType(uri) ?: "audio/*",
            size = 0,
            dateAdded = System.currentTimeMillis(),
            dateModified = System.currentTimeMillis(),
            artworkUri = null
        )
    }
    
    private fun checkMediaPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun requestMediaPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        permissionLauncher.launch(permissions)
    }
}
