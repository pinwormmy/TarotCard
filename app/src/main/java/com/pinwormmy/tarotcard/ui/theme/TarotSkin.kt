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

    val Twilight = TarotSkin(
        id = "twilight_moonstone",
        displayName = "Twilight Moonstone",
        backgroundBrush = Brush.verticalGradient(
            listOf(
                Color(0xFF0B0612),
                Color(0xFF1A0F28),
                Color(0xFF250F38)
            )
        ),
        backgroundOverlayColor = Color(0x88020210),
        primary = Color(0xFFC9A7EB),
        secondary = Color(0xFF7AD0F5),
        tertiary = Color(0xFFF7C1BB),
        surface = Color(0xFF161327),
        surfaceVariant = Color(0xFF2B2442),
        outline = Color(0xFF4B4371),
        textPrimary = Color(0xFFF1EBFF),
        textSecondary = Color(0xFFCDD2F7)
    )

    val Ember = TarotSkin(
        id = "ember_sands",
        displayName = "Ember Sands",
        backgroundBrush = Brush.verticalGradient(
            listOf(
                Color(0xFF120604),
                Color(0xFF2C0F08),
                Color(0xFF4A1F0F)
            )
        ),
        backgroundOverlayColor = Color(0xAA140600),
        primary = Color(0xFFFFC38F),
        secondary = Color(0xFFFF8F70),
        tertiary = Color(0xFFFFD86E),
        surface = Color(0xFF201512),
        surfaceVariant = Color(0xFF3A251D),
        outline = Color(0xFF5C3A2E),
        textPrimary = Color(0xFFFFF3E4),
        textSecondary = Color(0xFFFFD8C2)
    )

    val all: List<TarotSkin> = listOf(Midnight, Twilight, Ember)
    val default: TarotSkin = Midnight

    fun findById(id: String): TarotSkin = all.firstOrNull { it.id == id } ?: default
}

val LocalTarotSkin = staticCompositionLocalOf { TarotSkins.default }
