package com.pinwormmy.midoritarot.ui.components

import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.unit.Density

private const val DEFAULT_WINDOW_HEIGHT_DP = 800f

fun windowHeightDp(
    windowInfo: WindowInfo,
    density: Density,
    fallbackDp: Float = DEFAULT_WINDOW_HEIGHT_DP
): Float {
    val heightPx = windowInfo.containerSize.height
    return if (heightPx > 0) {
        heightPx / density.density
    } else {
        fallbackDp
    }
}
