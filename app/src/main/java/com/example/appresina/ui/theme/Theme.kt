package com.example.appresina.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ResinaSecondary,
    secondary = ResinaSecondaryVariant,
    tertiary = ResinaWarning,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = ResinaOnPrimary,
    onSecondary = ResinaOnSecondary,
    onBackground = Color(0xFFE1E1E1),
    onSurface = Color(0xFFE1E1E1),
    error = ResinaError
)

private val LightColorScheme = lightColorScheme(
    primary = ResinaPrimary,
    secondary = ResinaSecondary,
    tertiary = ResinaWarning,
    background = ResinaBackground,
    surface = ResinaSurface,
    surfaceVariant = ResinaSurfaceVariant,
    onPrimary = ResinaOnPrimary,
    onSecondary = ResinaOnSecondary,
    onBackground = ResinaOnBackground,
    onSurface = ResinaOnSurface,
    onSurfaceVariant = ResinaOnBackground,
    error = ResinaError
)

@Composable
fun AppResinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}