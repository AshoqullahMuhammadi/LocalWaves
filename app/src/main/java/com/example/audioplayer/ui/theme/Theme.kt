package com.example.audioplayer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom color palette - Vibrant teal and coral accent
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4FD1C5),          // Teal
    onPrimary = Color(0xFF003732),
    primaryContainer = Color(0xFF004D45),
    onPrimaryContainer = Color(0xFF70F7E9),
    secondary = Color(0xFFFF7B6B),         // Coral
    onSecondary = Color(0xFF4A1410),
    secondaryContainer = Color(0xFF6B2920),
    onSecondaryContainer = Color(0xFFFFDAD5),
    tertiary = Color(0xFFFFC947),          // Amber
    onTertiary = Color(0xFF3F2E00),
    tertiaryContainer = Color(0xFF5C4400),
    onTertiaryContainer = Color(0xFFFFE08C),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE1E2E5),
    surface = Color(0xFF1A1E22),
    onSurface = Color(0xFFE1E2E5),
    surfaceVariant = Color(0xFF252A2F),
    onSurfaceVariant = Color(0xFFC0C7CD),
    outline = Color(0xFF8A9196),
    inverseOnSurface = Color(0xFF2E3235),
    inverseSurface = Color(0xFFE1E2E5),
    inversePrimary = Color(0xFF006B62),
    surfaceTint = Color(0xFF4FD1C5)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006B62),          // Teal
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF70F7E9),
    onPrimaryContainer = Color(0xFF00201D),
    secondary = Color(0xFFB52A1C),         // Coral
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD5),
    onSecondaryContainer = Color(0xFF410003),
    tertiary = Color(0xFF7D5700),          // Amber
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDEA6),
    onTertiaryContainer = Color(0xFF271900),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8FAFA),
    onBackground = Color(0xFF191C1D),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF191C1D),
    surfaceVariant = Color(0xFFDBE4E5),
    onSurfaceVariant = Color(0xFF3F4949),
    outline = Color(0xFF6F7979),
    inverseOnSurface = Color(0xFFEFF1F1),
    inverseSurface = Color(0xFF2E3131),
    inversePrimary = Color(0xFF4FD1C5),
    surfaceTint = Color(0xFF006B62)
)

@Composable
fun AudioPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
