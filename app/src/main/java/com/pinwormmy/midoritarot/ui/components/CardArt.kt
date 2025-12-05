package com.pinwormmy.midoritarot.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.state.CardFaceSkin
import com.pinwormmy.midoritarot.ui.theme.LocalCardFaceSkin

@Suppress("DiscouragedApi", "UseOfNonComposableResources")
@Composable
fun rememberCardPainter(
    card: TarotCardModel,
    faceSkin: CardFaceSkin = LocalCardFaceSkin.current
): Painter? {
    val context = LocalContext.current
    val key = "${faceSkin.folder}:${card.imageUrl ?: card.id}"
    val assetPath = remember(key) {
        val name = (card.imageUrl ?: card.id).lowercase()
        "skins/${faceSkin.folder}/$name.jpg"
    }
    val assetPainter = remember(key, context) {
        runCatching {
            context.assets.open(assetPath).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }?.let { BitmapPainter(it) }
        }.getOrNull()
    }
    if (assetPainter != null) return assetPainter

    val resId = remember(key, context) {
        val normalized = (card.imageUrl ?: card.id).lowercase()
        context.resources.getIdentifier(normalized, "drawable", context.packageName)
            .takeIf { it != 0 }
    }
    return resId?.let { painterResource(id = it) }
}

@Suppress("DiscouragedApi", "UseOfNonComposableResources")
@Composable
fun rememberCardBackPainter(
    backStyle: com.pinwormmy.midoritarot.ui.state.CardBackStyle? = null
): Painter? {
    val context = LocalContext.current
    val style = backStyle ?: com.pinwormmy.midoritarot.ui.theme.LocalCardBackStyle.current
    val key = style.assetName
    val assetPath = remember(key) { "skins/cardback/${style.assetName.lowercase()}.jpg" }
    val assetPainter = remember(key, context) {
        runCatching {
            context.assets.open(assetPath).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }?.let { BitmapPainter(it) }
        }.getOrNull()
    }
    if (assetPainter != null) return assetPainter
    val resId = remember(key, context) {
        context.resources.getIdentifier(style.assetName.lowercase(), "drawable", context.packageName)
            .takeIf { it != 0 }
    }
    return resId?.let { painterResource(id = it) }
}

@Composable
fun CardBackArt(
    modifier: Modifier = Modifier,
    shape: Shape = TarotCardShape,
    overlay: Brush? = null,
    backStyle: com.pinwormmy.midoritarot.ui.state.CardBackStyle? = null,
    painterOverride: Painter? = null
) {
    val painter = painterOverride ?: rememberCardBackPainter(backStyle)
    if (painter != null) {
        Box(modifier = modifier) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
                painter = painter,
                contentDescription = null,
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
                        colors = listOf(Color(0xFF1C1D36), Color(0xFF0E0F1E))
                    ),
                    shape = shape
                )
        )
    }
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
