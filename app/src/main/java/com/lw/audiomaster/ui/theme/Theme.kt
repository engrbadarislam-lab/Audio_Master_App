package com.lw.audiomaster.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AudioColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = Abyss,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = TextPrimary,
    secondary = CyanGlow,
    onSecondary = Abyss,
    background = Abyss,
    onBackground = TextPrimary,
    surface = Surface1,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    error = DangerRed,
    onError = TextPrimary
)

@Composable
fun AudioMasterTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }
    MaterialTheme(
        colorScheme = AudioColorScheme,
        typography = AppTypography,
        content = content
    )
}
