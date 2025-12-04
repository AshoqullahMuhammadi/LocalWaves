package com.example.audioplayer.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    
    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val GAPLESS_PLAYBACK = booleanPreferencesKey("gapless_playback")
        private val CROSSFADE_DURATION = intPreferencesKey("crossfade_duration")
        private val DEFAULT_PLAYBACK_SPEED = floatPreferencesKey("default_playback_speed")
        private val PAUSE_ON_HEADSET_DISCONNECT = booleanPreferencesKey("pause_on_headset_disconnect")
        private val RESUME_AFTER_CALL = booleanPreferencesKey("resume_after_call")
        private val AUTO_SCAN_ON_STARTUP = booleanPreferencesKey("auto_scan_on_startup")
        private val SHOW_ALBUM_ART_ON_LOCKSCREEN = booleanPreferencesKey("show_album_art_on_lockscreen")
        private val KEEP_SCREEN_ON_WHILE_PLAYING = booleanPreferencesKey("keep_screen_on_while_playing")
        private val LAST_LIBRARY_TAB = intPreferencesKey("last_library_tab")
    }
    
    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        ThemeMode.valueOf(preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name)
    }
    
    val gaplessPlayback: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[GAPLESS_PLAYBACK] ?: true
    }
    
    val crossfadeDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CROSSFADE_DURATION] ?: 0
    }
    
    val defaultPlaybackSpeed: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_PLAYBACK_SPEED] ?: 1.0f
    }
    
    val pauseOnHeadsetDisconnect: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PAUSE_ON_HEADSET_DISCONNECT] ?: true
    }
    
    val resumeAfterCall: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[RESUME_AFTER_CALL] ?: true
    }
    
    val autoScanOnStartup: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_SCAN_ON_STARTUP] ?: true
    }
    
    val showAlbumArtOnLockscreen: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SHOW_ALBUM_ART_ON_LOCKSCREEN] ?: true
    }
    
    val keepScreenOnWhilePlaying: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEEP_SCREEN_ON_WHILE_PLAYING] ?: false
    }
    
    val lastLibraryTab: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LAST_LIBRARY_TAB] ?: 0
    }
    
    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name
        }
    }
    
    suspend fun setGaplessPlayback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GAPLESS_PLAYBACK] = enabled
        }
    }
    
    suspend fun setCrossfadeDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[CROSSFADE_DURATION] = duration
        }
    }
    
    suspend fun setDefaultPlaybackSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_PLAYBACK_SPEED] = speed
        }
    }
    
    suspend fun setPauseOnHeadsetDisconnect(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PAUSE_ON_HEADSET_DISCONNECT] = enabled
        }
    }
    
    suspend fun setResumeAfterCall(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[RESUME_AFTER_CALL] = enabled
        }
    }
    
    suspend fun setAutoScanOnStartup(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_SCAN_ON_STARTUP] = enabled
        }
    }
    
    suspend fun setShowAlbumArtOnLockscreen(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_ALBUM_ART_ON_LOCKSCREEN] = enabled
        }
    }
    
    suspend fun setKeepScreenOnWhilePlaying(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEEP_SCREEN_ON_WHILE_PLAYING] = enabled
        }
    }
    
    suspend fun setLastLibraryTab(tab: Int) {
        context.dataStore.edit { preferences ->
            preferences[LAST_LIBRARY_TAB] = tab
        }
    }
}

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

