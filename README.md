[![Stand With Palestine](https://raw.githubusercontent.com/TheBSD/StandWithPalestine/main/banner-no-action.svg)](https://thebsd.github.io/StandWithPalestine)

# LocalWave

A modern, feature-rich local audio player for Android built with Kotlin and Jetpack Compose.

LocalWave is designed for users who prefer to keep their music collection on their device. It provides a clean, intuitive interface with all the controls you need, without requiring an internet connection or subscription.

## Screenshots

| Home | Now Playing | Queue |
|:---:|:---:|:---:|
| ![Home Screen](screenshots/home.png) | ![Now Playing](screenshots/now_playing.png) | ![Queue](screenshots/queue.png) |

| Albums | Search | Settings |
|:---:|:---:|:---:|
| ![Albums](screenshots/albums.png) | ![Search](screenshots/search.png) | ![Settings](screenshots/settings.png) |

## Features

### Library Management

The app automatically scans your device for audio files and organizes them by tracks, albums, artists, and folders. You can also create custom playlists to group your favorite songs together. The home screen gives you quick access to your recently played tracks and albums.

### Background Playback

Music continues playing when you switch apps or lock your screen. The notification shows the current track with album art and lets you control playback without opening the app. Lock screen controls are also available on supported devices.

### Now Playing Screen

The full-screen player displays album artwork with a subtle glow effect. Controls include play/pause, skip forward and back, shuffle, and repeat modes. A seek bar lets you jump to any point in the track, and the time display updates in real time.

### Playback Speed Control

Adjust playback speed from 0.5x to 2.0x for audiobooks, podcasts, or when you want to listen at a different pace. The speed selector is available directly on the now playing screen.

### Queue Management

View and manage your upcoming tracks. You can reorder songs, remove tracks from the queue, or clear everything and start fresh. Tap any track in the queue to jump to it immediately.

### Playlists

Create playlists to organize your music however you like. Add tracks from anywhere in the app, reorder them with drag and drop, and rename or delete playlists when you no longer need them.

### Search

Find tracks, albums, artists, or playlists instantly. Results appear as you type, making it easy to locate specific songs in large collections.

### External File Support

Open audio files directly from your file manager or other apps. LocalWave will play the file and show a compact player dialog, letting you control playback without navigating away from what you were doing.

### Theme Support

Choose between light and dark themes, or let the app follow your system setting. The dark theme uses a modern gradient design that looks great on OLED screens.

## Supported Formats

- MP3
- M4A / AAC
- WAV
- FLAC
- OGG

Format support depends on your device's available codecs.

## Requirements

- Android 7.0 (API 24) or higher
- Storage permission to access audio files
- Notification permission (Android 13+) for playback controls

## Building from Source

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on your device or emulator

```bash
git clone https://github.com/yourusername/LocalWave.git
cd LocalWave
./gradlew assembleDebug
```

## Architecture

The app follows the MVVM architecture pattern with the following components:

- **UI Layer**: Jetpack Compose screens and components
- **ViewModel Layer**: Handles UI state and user interactions
- **Repository Layer**: Manages data from local database and MediaStore
- **Data Layer**: Room database for tracks, playlists, and playback state
- **Player Layer**: Media3 ExoPlayer with MediaSession for background playback

### Key Libraries

- Jetpack Compose for UI
- Media3 ExoPlayer for audio playback
- Room for local database
- DataStore for preferences
- Coil for image loading
- Navigation Compose for screen navigation

## Project Structure

```
app/src/main/java/com/example/audioplayer/
├── data/
│   ├── local/
│   │   ├── db/          # Room database and DAOs
│   │   └── preferences/ # DataStore preferences
│   ├── model/           # Data classes
│   └── repository/      # Data repositories
├── player/
│   ├── service/         # Media playback service
│   └── PlayerController.kt
├── ui/
│   ├── components/      # Reusable UI components
│   ├── navigation/      # Navigation setup
│   ├── screens/         # App screens
│   ├── theme/           # Colors, typography, theme
│   └── viewmodel/       # ViewModels
└── util/                # Utility classes
```

## Permissions

LocalWave requests the following permissions:

- **READ_MEDIA_AUDIO** (Android 13+) or **READ_EXTERNAL_STORAGE** (older versions): Required to access audio files on your device
- **POST_NOTIFICATIONS** (Android 13+): Required to show playback controls in the notification area
- **FOREGROUND_SERVICE**: Required to continue playback when the app is in the background
- **WAKE_LOCK**: Prevents the device from sleeping during playback

All audio files are accessed locally. No data is uploaded to external servers.

## Known Limitations

- Streaming from URLs is not supported in this version
- DRM-protected files cannot be played
- Equalizer and audio effects are planned for a future release

## Contributing

Contributions are welcome. Please open an issue first to discuss what you would like to change.

## License

This project is available under the MIT License. See the LICENSE file for details.

---

Built with Kotlin and Jetpack Compose for Android.

