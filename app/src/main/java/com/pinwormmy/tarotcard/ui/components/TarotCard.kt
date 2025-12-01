package com.pinwormmy.tarotcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.components.CardFaceArt

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
            .clip(RoundedCornerShape(20.dp))
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
            .aspectRatio(2f / 3f)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF14162F), Color(0xFF050612))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFB59BFF), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(16.dp)
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
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF130F2A))
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
