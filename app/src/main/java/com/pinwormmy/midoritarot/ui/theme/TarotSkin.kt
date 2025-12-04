package com.pinwormmy.midoritarot.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.pinwormmy.midoritarot.R

@Immutable
data class TarotSkin(
    val id: String,
    val displayName: String,
    val backgroundBrush: Brush,
    val backgroundOverlayColor: Color,
    val backgroundImageRes: Int? = null,
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
    val SpaceOne = TarotSkin(
        id = "space_1",
        displayName = "우주1",
        backgroundBrush = Brush.verticalGradient(
            listOf(
                Color(0xFF020203),
                Color(0xFF070817),
                Color(0xFF0F1230)
            )
        ),
        backgroundOverlayColor = Color(0x33000000),
        backgroundImageRes = R.drawable.bg_space01,
        primary = Color(0xFFE0C097),
        secondary = Color(0xFF8A8AE6),
        tertiary = Color(0xFFF5B9A3),
        surface = Color(0xFF111323),
        surfaceVariant = Color(0xFF1C1F33),
        outline = Color(0xFF3C3D63),
        textPrimary = Color(0xFFF4EADB),
        textSecondary = Color(0xFFC9CDE2)
    )

    val SpaceTwo = TarotSkin(
        id = "space_2",
        displayName = "우주2",
        backgroundBrush = Brush.verticalGradient(
            listOf(
                Color(0xFF05060F),
                Color(0xFF0A0D1C),
                Color(0xFF0F1229)
            )
        ),
        backgroundOverlayColor = Color(0x33020210),
        backgroundImageRes = R.drawable.bg_space02,
        primary = Color(0xFFC9A7EB),
        secondary = Color(0xFF7AD0F5),
        tertiary = Color(0xFFF7C1BB),
        surface = Color(0xFF161327),
        surfaceVariant = Color(0xFF2B2442),
        outline = Color(0xFF4B4371),
        textPrimary = Color(0xFFF1EBFF),
        textSecondary = Color(0xFFCDD2F7)
    )

    val all: List<TarotSkin> = listOf(SpaceOne, SpaceTwo)
    val default: TarotSkin = SpaceOne

    fun findById(id: String): TarotSkin = all.firstOrNull { it.id == id } ?: default
}

val LocalTarotSkin = staticCompositionLocalOf { TarotSkins.default }
