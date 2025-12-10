package com.pinwormmy.midoritarot.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("DiscouragedApi", "UseOfNonComposableResources")
@Composable
fun rememberCardPainter(
    card: TarotCardModel,
    faceSkin: CardFaceSkin = LocalCardFaceSkin.current
): Painter? {
    val context = LocalContext.current
    val normalized = remember(card.id, card.imageUrl) {
        (card.imageUrl ?: card.id).lowercase()
    }
    val key = "${faceSkin.folder}:$normalized"
    val resId = remember(key, context) {
        context.resources.getIdentifier(normalized, "drawable", context.packageName)
            .takeIf { it != 0 }
    }
    if (resId != null) return painterResource(id = resId)

    val assetPath = remember(key) { "skins/${faceSkin.folder}/$normalized.jpg" }
    return rememberAssetBitmapPainter(key = key, assetPath = assetPath)
}

@Suppress("DiscouragedApi", "UseOfNonComposableResources")
@Composable
fun rememberCardBackPainter(
    backStyle: com.pinwormmy.midoritarot.ui.state.CardBackStyle? = null
): Painter? {
    val context = LocalContext.current
    val style = backStyle ?: com.pinwormmy.midoritarot.ui.theme.LocalCardBackStyle.current
    val key = style.assetName.lowercase()
    val resId = remember(key, context) {
        context.resources.getIdentifier(key, "drawable", context.packageName)
            .takeIf { it != 0 }
    }
    if (resId != null) return painterResource(id = resId)

    val assetPath = remember(key) { "skins/cardback/$key.jpg" }
    return rememberAssetBitmapPainter(key = key, assetPath = assetPath)
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
private fun rememberAssetBitmapPainter(
    key: String,
    assetPath: String
): Painter? {
    val context = LocalContext.current
    var painter by remember(key) { mutableStateOf<Painter?>(null) }

    LaunchedEffect(key, assetPath, context) {
        if (painter != null) return@LaunchedEffect
        val bitmap = withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open(assetPath).use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
        painter = bitmap?.let { BitmapPainter(it) }
    }

    return painter
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
