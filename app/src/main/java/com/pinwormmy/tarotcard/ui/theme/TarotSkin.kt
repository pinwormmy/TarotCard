package com.pinwormmy.tarotcard.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Immutable
data class TarotSkin(
    val id: String,
    val displayName: String,
    val backgroundBrush: Brush,
    val backgroundOverlayColor: Color,
    val backgroundImage: Painter? = null,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

object TarotSkins {
    val Midnight = TarotSkin(
        id = "midnight_obsidian",
        displayName = "Midnight Obsidian",
        backgroundBrush = Brush.verticalGradient(
            listOf(
                Color(0xFF020203),
                Color(0xFF070817),
                Color(0xFF0F1230)
            )
        ),
        backgroundOverlayColor = Color(0x99000000),
        primary = Color(0xFFE0C097),
        secondary = Color(0xFF8A8AE6),
        tertiary = Color(0xFFF5B9A3),
        surface = Color(0xFF111323),
        surfaceVariant = Color(0xFF1C1F33),
        outline = Color(0xFF3C3D63),
        textPrimary = Color(0xFFF4EADB),
        textSecondary = Color(0xFFC9CDE2)
    )
}

val LocalTarotSkin = staticCompositionLocalOf { TarotSkins.Midnight }
