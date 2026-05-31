package com.example.moodit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

object SharedLocationHolder {
    var location: String = "위치 정보를 불러올 수 없습니다."
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.White,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color.White,
    background = DarkBg,
    onBackground = Color(0xFFF2EEF7),
    surface = DarkSurface,
    onSurface = Color(0xFFF2EEF7),
    outline = DarkOutline,
    surfaceVariant = Color(0xFF2C2C35),
    onSurfaceVariant = Color(0xFF9E9E9E)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = Color.Black,
    background = LightBg,
    onBackground = Color.Black,
    surface = LightSurface,
    onSurface = Color.Black,
    outline = LightOutline,
    surfaceVariant = Color(0xFFF1E6FF),
    onSurfaceVariant = Color.Gray
)

@Composable
fun MooditTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled by default to enforce our custom design system colors
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}