package com.example.audioplayer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.audioplayer.data.local.preferences.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    gaplessPlayback: Boolean,
    crossfadeDuration: Int,
    defaultPlaybackSpeed: Float,
    pauseOnHeadsetDisconnect: Boolean,
    resumeAfterCall: Boolean,
    autoScanOnStartup: Boolean,
    onBackClick: () -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onGaplessPlaybackChange: (Boolean) -> Unit,
    onCrossfadeDurationChange: (Int) -> Unit,
    onDefaultPlaybackSpeedChange: (Float) -> Unit,
    onPauseOnHeadsetDisconnectChange: (Boolean) -> Unit,
    onResumeAfterCallChange: (Boolean) -> Unit,
    onAutoScanOnStartupChange: (Boolean) -> Unit,
    onRescanLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Appearance Section
            item {
                SettingsSectionHeader(title = "Appearance")
            }
            
            item {
                SettingsItem(
                    title = "Theme",
                    subtitle = themeMode.name.lowercase().replaceFirstChar { it.uppercase() },
                    icon = Icons.Default.Palette,
                    onClick = { showThemeDialog = true }
                )
            }
            
            // Playback Section
            item {
                SettingsSectionHeader(title = "Playback")
            }
            
            item {
                SettingsSwitchItem(
                    title = "Gapless playback",
                    subtitle = "Play tracks without gaps between them",
                    icon = Icons.Default.GraphicEq,
                    checked = gaplessPlayback,
                    onCheckedChange = onGaplessPlaybackChange
                )
            }
            
            item {
                SettingsItem(
                    title = "Default playback speed",
                    subtitle = "${defaultPlaybackSpeed}x",
                    icon = Icons.Default.Speed,
                    onClick = { showSpeedDialog = true }
                )
            }
            
            // Audio Focus Section
            item {
                SettingsSectionHeader(title = "Audio Focus")
            }
            
            item {
                SettingsSwitchItem(
                    title = "Pause on headset disconnect",
                    subtitle = "Automatically pause when headphones are unplugged",
                    icon = Icons.Default.Headphones,
                    checked = pauseOnHeadsetDisconnect,
                    onCheckedChange = onPauseOnHeadsetDisconnectChange
                )
            }
            
            item {
                SettingsSwitchItem(
                    title = "Resume after phone call",
                    subtitle = "Automatically resume playback after a call ends",
                    icon = Icons.Default.Phone,
                    checked = resumeAfterCall,
                    onCheckedChange = onResumeAfterCallChange
                )
            }
            
            // Library Section
            item {
                SettingsSectionHeader(title = "Library")
            }
            
            item {
                SettingsSwitchItem(
                    title = "Auto scan on startup",
                    subtitle = "Automatically scan for new audio files when app opens",
                    icon = Icons.Default.Refresh,
                    checked = autoScanOnStartup,
                    onCheckedChange = onAutoScanOnStartupChange
                )
            }
            
            item {
                SettingsItem(
                    title = "Rescan library",
                    subtitle = "Scan device for audio files",
                    icon = Icons.Default.FolderOpen,
                    onClick = onRescanLibrary
                )
            }
            
            // About Section
            item {
                SettingsSectionHeader(title = "About")
            }
            
            item {
                SettingsItem(
                    title = "LocalWave",
                    subtitle = "Version 1.0.0",
                    icon = Icons.Default.Info,
                    onClick = { }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onThemeChange(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = {
                                    onThemeChange(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = mode.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Playback Speed Dialog
    if (showSpeedDialog) {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Default Playback Speed") },
            text = {
                Column {
                    speeds.forEach { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDefaultPlaybackSpeedChange(speed)
                                    showSpeedDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = defaultPlaybackSpeed == speed,
                                onClick = {
                                    onDefaultPlaybackSpeedChange(speed)
                                    showSpeedDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${speed}x")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

