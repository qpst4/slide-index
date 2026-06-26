package com.slideindex.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SlideIndexTheme(
    seedColor: Color = Purple40,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = seedColor,
            onPrimary = Color.White,
            surface = SurfaceDark.copy(alpha = 0.92f),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF49454F),
        )
    } else {
        lightColorScheme(
            primary = seedColor,
            onPrimary = Color.White,
            surface = SurfaceLight.copy(alpha = 0.95f),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFE7E0EC),
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SlideIndexTypography,
        content = content,
    )
}
