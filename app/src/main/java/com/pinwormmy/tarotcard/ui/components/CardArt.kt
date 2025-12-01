package com.pinwormmy.tarotcard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.components.TarotCardShape

@Composable
fun rememberCardPainter(card: TarotCardModel): Painter? {
    val context = LocalContext.current
    val key = card.imageUrl ?: card.id
    val resId = remember(key, context) {
        key?.let { normalized ->
            context.resources.getIdentifier(normalized.lowercase(), "drawable", context.packageName)
        }?.takeIf { it != 0 }
    }
    return resId?.let { painterResource(id = it) }
}

@Composable
fun CardFaceArt(
    card: TarotCardModel,
    modifier: Modifier = Modifier,
    overlay: Brush? = null,
    shape: Shape = TarotCardShape
 ) {
    val painter = rememberCardPainter(card)
    if (painter != null) {
        Box(modifier = modifier) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
                painter = painter,
                contentDescription = card.name,
                contentScale = ContentScale.Crop
            )
            if (overlay != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(overlay, shape = shape)
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF3D3E65), Color(0xFF1A1B30))
                    ),
                    shape = shape
                )
        )
    }
}
