package com.pinwormmy.midoritarot.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn

const val CARD_ASPECT_RATIO = 2f / 3f
const val CARD_LANDSCAPE_RATIO = 1f / CARD_ASPECT_RATIO

private const val CARD_PHYSICAL_WIDTH_MM = 800f
private const val CARD_PHYSICAL_HEIGHT_MM = 1200f
private const val MM_TO_DP = 160f / 25.4f

private val CARD_PHYSICAL_MAX_WIDTH_DP = (CARD_PHYSICAL_WIDTH_MM * MM_TO_DP).dp
private val CARD_PHYSICAL_MAX_HEIGHT_DP = (CARD_PHYSICAL_HEIGHT_MM * MM_TO_DP).dp

data class CardSizeLimit(val maxWidth: Dp, val maxHeight: Dp)

fun computeCardSizeLimit(
    screenHeightDp: Int,
    scaleFactor: Float,
    heightFraction: Float = 0.7f
): CardSizeLimit {
    val physicalMaxHeight = CARD_PHYSICAL_MAX_HEIGHT_DP / scaleFactor
    val physicalMaxWidth = CARD_PHYSICAL_MAX_WIDTH_DP / scaleFactor
    val screenLimitedHeight = (screenHeightDp * heightFraction).dp
    val maxHeight = minOf(physicalMaxHeight, screenLimitedHeight)
    val maxWidth = minOf(maxHeight * CARD_ASPECT_RATIO, physicalMaxWidth)
    return CardSizeLimit(maxWidth = maxWidth, maxHeight = maxHeight)
}

fun Modifier.applyCardSizeLimit(limit: CardSizeLimit): Modifier =
    this.widthIn(max = limit.maxWidth).heightIn(max = limit.maxHeight)
