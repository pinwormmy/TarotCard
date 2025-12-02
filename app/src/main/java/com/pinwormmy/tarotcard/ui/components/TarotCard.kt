package com.pinwormmy.tarotcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.components.CardFaceArt
import com.pinwormmy.tarotcard.ui.components.CardBackArt
import com.pinwormmy.tarotcard.ui.components.TarotCardShape

@Composable
fun TarotCard(
    card: TarotCardModel,
    isFaceUp: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    flipDurationMillis: Int = 450
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFaceUp) 180f else 0f,
        animationSpec = tween(flipDurationMillis),
        label = "tarotCardFlip"
    )

    Surface(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(TarotCardShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12 * density
                },
            contentAlignment = Alignment.Center
        ) {
            CardBackFace(
                modifier = Modifier.graphicsLayer {
                    alpha = if (rotation >= 90f) 0f else 1f
                }
            )
            CardFrontFace(
                card = card,
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f
                    alpha = if (rotation >= 90f) 1f else 0f
                }
            )
        }
    }
}

@Composable
private fun CardBackFace(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f),
        contentAlignment = Alignment.Center
    ) {
        CardBackArt(
            modifier = Modifier.fillMaxSize(),
            overlay = Brush.verticalGradient(
                listOf(Color.Transparent, Color(0x88000000))
            )
        )
    }
}

@Composable
private fun CardFrontFace(
    card: TarotCardModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(TarotCardShape)
    ) {
        CardFaceArt(
            card = card,
            modifier = Modifier.fillMaxSize(),
            overlay = Brush.verticalGradient(
                listOf(Color.Transparent, Color(0xCC0C0B18))
            )
        )
    }
}
