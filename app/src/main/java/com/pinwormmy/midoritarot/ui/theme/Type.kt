package com.pinwormmy.midoritarot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.pinwormmy.midoritarot.R

private val aritaBuri = FontFamily(
    Font(R.font.aritaburikr_hairline, FontWeight.Thin),
    Font(R.font.aritaburikr_light, FontWeight.Light),
    Font(R.font.aritaburikr_regular, FontWeight.Normal),
    Font(R.font.aritaburikr_medium, FontWeight.Medium),
    Font(R.font.aritaburikr_semibold, FontWeight.SemiBold),
    Font(R.font.aritaburikr_bold, FontWeight.Bold)
)

private val baseTypography = Typography()

val Typography = baseTypography.copy(
    displayLarge = baseTypography.displayLarge.copy(fontFamily = aritaBuri),
    displayMedium = baseTypography.displayMedium.copy(fontFamily = aritaBuri),
    displaySmall = baseTypography.displaySmall.copy(fontFamily = aritaBuri),
    headlineLarge = baseTypography.headlineLarge.copy(fontFamily = aritaBuri),
    headlineMedium = baseTypography.headlineMedium.copy(fontFamily = aritaBuri),
    headlineSmall = baseTypography.headlineSmall.copy(fontFamily = aritaBuri),
    titleLarge = baseTypography.titleLarge.copy(fontFamily = aritaBuri),
    titleMedium = baseTypography.titleMedium.copy(fontFamily = aritaBuri),
    titleSmall = baseTypography.titleSmall.copy(fontFamily = aritaBuri),
    bodyLarge = baseTypography.bodyLarge.copy(fontFamily = aritaBuri),
    bodyMedium = baseTypography.bodyMedium.copy(fontFamily = aritaBuri),
    bodySmall = baseTypography.bodySmall.copy(fontFamily = aritaBuri),
    labelLarge = baseTypography.labelLarge.copy(fontFamily = aritaBuri),
    labelMedium = baseTypography.labelMedium.copy(fontFamily = aritaBuri),
    labelSmall = baseTypography.labelSmall.copy(fontFamily = aritaBuri)
)

private fun TextStyle.scale(factor: Float): TextStyle = copy(
    fontSize = fontSize * factor,
    lineHeight = lineHeight * factor,
    letterSpacing = if (letterSpacing == TextUnit.Unspecified) letterSpacing else letterSpacing * factor
)

fun Typography.scaled(factor: Float): Typography = copy(
    displayLarge = displayLarge.scale(factor),
    displayMedium = displayMedium.scale(factor),
    displaySmall = displaySmall.scale(factor),
    headlineLarge = headlineLarge.scale(factor),
    headlineMedium = headlineMedium.scale(factor),
    headlineSmall = headlineSmall.scale(factor),
    titleLarge = titleLarge.scale(factor),
    titleMedium = titleMedium.scale(factor),
    titleSmall = titleSmall.scale(factor),
    bodyLarge = bodyLarge.scale(factor),
    bodyMedium = bodyMedium.scale(factor),
    bodySmall = bodySmall.scale(factor),
    labelLarge = labelLarge.scale(factor),
    labelMedium = labelMedium.scale(factor),
    labelSmall = labelSmall.scale(factor)
)

private const val TABLET_TYPE_SCALE = 1.12f

val TypographyTablet: Typography = Typography.scaled(TABLET_TYPE_SCALE)
