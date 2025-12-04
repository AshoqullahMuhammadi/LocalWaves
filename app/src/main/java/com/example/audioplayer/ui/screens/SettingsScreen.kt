package com.example.audioplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.audioplayer.data.local.preferences.ThemeMode
import com.example.audioplayer.ui.theme.GradientColors

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
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(GradientColors.darkBackground))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
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
            }
        }
    }
    
    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme", color = Color.White) },
            containerColor = Color(0xFF1A1A2E),
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
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFE91E63)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = Color.White
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel", color = Color(0xFFE91E63))
                }
            }
        )
    }
    
    // Playback Speed Dialog
    if (showSpeedDialog) {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Default Playback Speed", color = Color.White) },
            containerColor = Color(0xFF1A1A2E),
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
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFE91E63)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${speed}x", color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Cancel", color = Color(0xFFE91E63))
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
        color = Color(0xFFE91E63),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
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
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
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
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFE91E63),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}
