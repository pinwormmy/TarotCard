package com.pinwormmy.midoritarot.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalDensity
import com.pinwormmy.midoritarot.ui.state.SpreadDefinition
import com.pinwormmy.midoritarot.ui.state.SpreadLayout
import com.pinwormmy.midoritarot.ui.state.SpreadPosition
import kotlin.math.max
import kotlin.math.min

private const val CARD_ASPECT_RATIO = 0.62f

@Composable
fun SpreadBoard(
    layout: SpreadLayout,
    positions: List<SpreadPosition>,
    modifier: Modifier = Modifier,
    spacing: Dp = 16.dp,
    content: @Composable (index: Int, position: SpreadPosition, modifier: Modifier) -> Unit
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val spacingPx = with(density) { spacing.toPx() }
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }
        val columnCount = max(layout.columns, 1)
        val rowCount = max(layout.rows, 1)
        val usableWidth = (maxWidthPx - spacingPx * (columnCount - 1)).coerceAtLeast(0f)
        val usableHeight = (maxHeightPx - spacingPx * (rowCount - 1)).coerceAtLeast(0f)
        val widthBased = usableWidth / columnCount
        val widthFromHeight = (usableHeight / rowCount) * CARD_ASPECT_RATIO
        val cardWidthPx = min(widthBased, widthFromHeight).coerceAtLeast(80f)
        val cardHeightPx = cardWidthPx / CARD_ASPECT_RATIO
        val contentWidthPx = columnCount * cardWidthPx + spacingPx * (columnCount - 1)
        val contentHeightPx = rowCount * cardHeightPx + spacingPx * (rowCount - 1)
        val startXPx = ((maxWidthPx - contentWidthPx) / 2f).coerceAtLeast(0f)
        val startYPx = ((maxHeightPx - contentHeightPx) / 2f).coerceAtLeast(0f)
        val cardWidth = with(density) { cardWidthPx.toDp() }
        val cardHeight = with(density) { cardHeightPx.toDp() }

        positions.forEachIndexed { index, position ->
            val offsetX = startXPx + position.placement.column * (cardWidthPx + spacingPx)
            val offsetY = startYPx + position.placement.row * (cardHeightPx + spacingPx)
            val modifierForCard = Modifier
                .offset(x = with(density) { offsetX.toDp() }, y = with(density) { offsetY.toDp() })
                .size(width = cardWidth, height = cardHeight)
                .graphicsLayer { rotationZ = position.placement.rotationDegrees }
                .zIndex(position.placement.zIndex)
            content(index, position, modifierForCard)
        }
    }
}

fun SpreadDefinition.estimatedBoardHeight(): Dp = when (layout.rows) {
    1 -> 240.dp
    2 -> 300.dp
    3 -> 360.dp
    4 -> 420.dp
    else -> 420.dp + ((layout.rows - 4) * 60).dp
}
