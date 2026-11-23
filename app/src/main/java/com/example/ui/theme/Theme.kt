package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val HisaabDarkColorScheme = darkColorScheme(
    primary = OperatorAmber,
    onPrimary = OperatorTextDark,
    primaryContainer = GlowIndigo,
    onPrimaryContainer = TextPrimary,
    secondary = EqualsGold,
    onSecondary = OperatorTextDark,
    tertiary = AccentCoral,
    onTertiary = TextPrimary,
    background = MidnightBg,
    onBackground = TextPrimary,
    surface = MidnightSurface,
    onSurface = TextPrimary,
    surfaceVariant = MidnightSheet,
    onSurfaceVariant = TextMuted,
    outline = GlassBorder,
    error = AccentCoral,
    onError = TextPrimary,
)

val LocalHisaabColors = staticCompositionLocalOf { HisaabColors() }

object HisaabTheme {
    val colors: HisaabColors
        @Composable
        @ReadOnlyComposable
        get() = LocalHisaabColors.current
}

@Composable
fun HisaabKitaabTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalHisaabColors provides HisaabColors()) {
        MaterialTheme(
            colorScheme = HisaabDarkColorScheme,
            typography = Typography,
            content = content,
        )
    }
}
