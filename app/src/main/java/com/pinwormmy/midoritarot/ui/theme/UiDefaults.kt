@file:OptIn(ExperimentalMaterial3Api::class)

package com.pinwormmy.midoritarot.ui.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Centralized UI tokens to keep screen styling consistent.
 */
object TarotUiDefaults {
    val panelShape = RoundedCornerShape(24.dp)
    val sheetShape = RoundedCornerShape(28.dp)

    @Composable
    fun scrimColor(alpha: Float = 0.72f): Color =
        MaterialTheme.colorScheme.scrim.copy(alpha = alpha)

    @Composable
    fun panelColor(alpha: Float = 0.9f): Color =
        MaterialTheme.colorScheme.surface.copy(alpha = alpha)

    @Composable
    fun secondaryPanelColor(alpha: Float = 0.9f): Color =
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)

    @Composable
    fun outline(alpha: Float = 0.4f): Color =
        MaterialTheme.colorScheme.outline.copy(alpha = alpha)

    @Composable
    fun hint(alpha: Float = 0.7f): Color =
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)

    @Composable
    fun panelBorder(alpha: Float = 0.18f): BorderStroke =
        BorderStroke(1.dp, outline(alpha))

    // Default TopAppBar
    @Composable
    fun topBarColors(): TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.62f)
        )

    // Center aligned TopAppBar colors (Material3 최신 API)
    @Composable
    fun centerTopBarColors(): TopAppBarColors =
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.62f)
        )
}
