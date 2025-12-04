package com.example.audioplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.local.preferences.ThemeMode
import com.example.audioplayer.data.local.preferences.UserPreferences
import com.example.audioplayer.localWaveApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userPreferences: UserPreferences = application.localWaveApp.userPreferences
    
    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.Lazily, ThemeMode.SYSTEM)
    
    val gaplessPlayback: StateFlow<Boolean> = userPreferences.gaplessPlayback
        .stateIn(viewModelScope, SharingStarted.Lazily, true)
    
    val crossfadeDuration: StateFlow<Int> = userPreferences.crossfadeDuration
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)
    
    val defaultPlaybackSpeed: StateFlow<Float> = userPreferences.defaultPlaybackSpeed
        .stateIn(viewModelScope, SharingStarted.Lazily, 1.0f)
    
    val pauseOnHeadsetDisconnect: StateFlow<Boolean> = userPreferences.pauseOnHeadsetDisconnect
        .stateIn(viewModelScope, SharingStarted.Lazily, true)
    
    val resumeAfterCall: StateFlow<Boolean> = userPreferences.resumeAfterCall
        .stateIn(viewModelScope, SharingStarted.Lazily, true)
    
    val autoScanOnStartup: StateFlow<Boolean> = userPreferences.autoScanOnStartup
        .stateIn(viewModelScope, SharingStarted.Lazily, true)
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }
    
    fun setGaplessPlayback(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setGaplessPlayback(enabled)
        }
    }
    
    fun setCrossfadeDuration(duration: Int) {
        viewModelScope.launch {
            userPreferences.setCrossfadeDuration(duration)
        }
    }
    
    fun setDefaultPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            userPreferences.setDefaultPlaybackSpeed(speed)
        }
    }
    
    fun setPauseOnHeadsetDisconnect(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setPauseOnHeadsetDisconnect(enabled)
        }
    }
    
    fun setResumeAfterCall(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setResumeAfterCall(enabled)
        }
    }
    
    fun setAutoScanOnStartup(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setAutoScanOnStartup(enabled)
        }
    }
}

