package com.pinwormmy.midoritarot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.Color
import com.pinwormmy.midoritarot.ui.state.CardBackStyle
import com.pinwormmy.midoritarot.ui.state.CardFaceSkin
import com.pinwormmy.midoritarot.ui.components.windowHeightDp

private fun colorSchemeForSkin(skin: TarotSkin) = darkColorScheme(
    primary = skin.primary,
    onPrimary = Color.White,
    primaryContainer = skin.primary.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,
    secondary = skin.secondary,
    onSecondary = Color.White,
    secondaryContainer = skin.secondary.copy(alpha = 0.25f),
    onSecondaryContainer = Color.White,
    tertiary = skin.tertiary,
    onTertiary = Color.White,
    background = Color(0xFF05060F),
    onBackground = skin.textPrimary,
    surface = skin.surface,
    onSurface = skin.textPrimary,
    surfaceVariant = skin.surfaceVariant,
    onSurfaceVariant = skin.textSecondary,
    outline = skin.outline
)

val LocalUiHeightScale = staticCompositionLocalOf { 1f }

@Composable
fun TarotcardTheme(
    skin: TarotSkin = TarotSkins.default,
    cardFaceSkin: CardFaceSkin = CardFaceSkin.Animation,
    cardBackStyle: CardBackStyle = CardBackStyle.Byzantine,
    content: @Composable () -> Unit
) {
    val colorScheme = colorSchemeForSkin(skin)
    val basePhoneHeightDp = 800f
    val windowInfo = LocalWindowInfo.current
    val baseDensity = LocalDensity.current
    val windowHeightDp = windowHeightDp(windowInfo, baseDensity, basePhoneHeightDp)
    val scaleFactor = (windowHeightDp / basePhoneHeightDp).coerceIn(1f, 2.5f)
    val typography = Typography

    val scaledDensity = Density(
        density = baseDensity.density * scaleFactor,
        fontScale = baseDensity.fontScale
    )

    CompositionLocalProvider(
        LocalTarotSkin provides skin,
        LocalCardFaceSkin provides cardFaceSkin,
        LocalCardBackStyle provides cardBackStyle,
        LocalUiHeightScale provides scaleFactor,
        LocalDensity provides scaledDensity
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
