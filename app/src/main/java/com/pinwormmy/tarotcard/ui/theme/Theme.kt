package com.pinwormmy.tarotcard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private fun colorSchemeForSkin(skin: TarotSkin) = darkColorScheme(
    primary = skin.primary,
    onPrimary = Color.Black,
    primaryContainer = skin.primary.copy(alpha = 0.2f),
    onPrimaryContainer = skin.primary,
    secondary = skin.secondary,
    onSecondary = Color.Black,
    secondaryContainer = skin.secondary.copy(alpha = 0.25f),
    onSecondaryContainer = skin.secondary,
    tertiary = skin.tertiary,
    onTertiary = Color.Black,
    background = Color(0xFF05060F),
    onBackground = skin.textPrimary,
    surface = skin.surface,
    onSurface = skin.textPrimary,
    surfaceVariant = skin.surfaceVariant,
    onSurfaceVariant = skin.textSecondary,
    outline = skin.outline
)

@Composable
fun TarotcardTheme(
    skin: TarotSkin = TarotSkins.Midnight,
    content: @Composable () -> Unit
) {
    val colorScheme = colorSchemeForSkin(skin)

    CompositionLocalProvider(LocalTarotSkin provides skin) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
