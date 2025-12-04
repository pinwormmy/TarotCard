package com.pinwormmy.midoritarot.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.LocalContentColor

@Composable
fun TarotBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val skin = LocalTarotSkin.current
    Box(
        modifier = modifier
            .background(Color.Black)
            .background(skin.backgroundBrush)
            .fillMaxSize()
    ) {
        skin.backgroundImageRes?.let { resId ->
            androidx.compose.foundation.Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                alpha = 0.4f,
                contentScale = ContentScale.Crop
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(skin.backgroundOverlayColor)
        )
        CompositionLocalProvider(
            LocalContentColor provides skin.textPrimary
        ) {
            content()
        }
    }
}
