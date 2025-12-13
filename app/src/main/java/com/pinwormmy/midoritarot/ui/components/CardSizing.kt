package com.pinwormmy.midoritarot.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.pinwormmy.midoritarot.ui.theme.LocalUiHeightScale

@Composable
fun rememberCardSizeLimit(
    heightFraction: Float = 0.7f
): CardSizeLimit {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val scaleFactor = LocalUiHeightScale.current
    val screenHeightDp = windowHeightDp(windowInfo, density).toInt()
    return remember(windowInfo.containerSize.height, scaleFactor, heightFraction, screenHeightDp) {
        computeCardSizeLimit(
            screenHeightDp = screenHeightDp,
            scaleFactor = scaleFactor,
            heightFraction = heightFraction
        )
    }
}

