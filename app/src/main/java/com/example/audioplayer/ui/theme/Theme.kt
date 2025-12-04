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

// Modern dark theme with vibrant accents - matching the design
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE91E63),              // Pink/Magenta accent
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF880E4F),
    onPrimaryContainer = Color(0xFFFFD9E4),
    secondary = Color(0xFF00E5FF),             // Cyan accent
    onSecondary = Color(0xFF003640),
    secondaryContainer = Color(0xFF004D5C),
    onSecondaryContainer = Color(0xFF97F0FF),
    tertiary = Color(0xFFBB86FC),              // Purple accent
    onTertiary = Color(0xFF3700B3),
    tertiaryContainer = Color(0xFF4A148C),
    onTertiaryContainer = Color(0xFFE8DEF8),
    error = Color(0xFFFF5252),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0D0D0D),            // Near black
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1A1A2E),               // Dark blue-ish surface
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF252542),        // Slightly lighter
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    inverseOnSurface = Color(0xFF1C1B1F),
    inverseSurface = Color(0xFFE6E1E5),
    inversePrimary = Color(0xFFB31B5A),
    surfaceTint = Color(0xFFE91E63)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD81B60),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9E4),
    onPrimaryContainer = Color(0xFF3E001D),
    secondary = Color(0xFF00ACC1),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF002022),
    tertiary = Color(0xFF7C4DFF),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE8DEF8),
    onTertiaryContainer = Color(0xFF21005D),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    inverseOnSurface = Color(0xFFF4EFF4),
    inverseSurface = Color(0xFF313033),
    inversePrimary = Color(0xFFFFB1C8),
    surfaceTint = Color(0xFFD81B60)
)

@Composable
fun AudioPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
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

// Gradient colors for backgrounds
object GradientColors {
    val darkBackground = listOf(
        Color(0xFF0D0D0D),
        Color(0xFF1A1A2E),
        Color(0xFF16213E)
    )
    
    val cardGradient = listOf(
        Color(0xFF2D2D44),
        Color(0xFF1A1A2E)
    )
    
    val pinkAccent = listOf(
        Color(0xFFE91E63),
        Color(0xFFAD1457)
    )
    
    val purpleAccent = listOf(
        Color(0xFF9C27B0),
        Color(0xFF6A1B9A)
    )
    
    val cyanAccent = listOf(
        Color(0xFF00BCD4),
        Color(0xFF00838F)
    )
}
