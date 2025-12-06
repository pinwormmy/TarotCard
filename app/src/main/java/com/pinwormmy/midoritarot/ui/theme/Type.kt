package com.pinwormmy.midoritarot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
