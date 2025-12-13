package com.pinwormmy.midoritarot.assets

import android.content.Context
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.state.CardBackStyle
import com.pinwormmy.midoritarot.ui.state.CardFaceSkin

fun normalizedFaceName(card: TarotCardModel): String {
    val raw = card.imageUrl ?: card.id
    val lastSegment = raw.substringAfterLast('/').substringAfterLast('\\')
    val withoutQuery = lastSegment.substringBefore('?').substringBefore('#')
    val withoutExtension = withoutQuery.substringBeforeLast('.', missingDelimiterValue = withoutQuery)
    return withoutExtension.lowercase()
}

fun faceDrawableResId(context: Context, name: String): Int? {
    val resId = context.resources.getIdentifier(name.lowercase(), "drawable", context.packageName)
    return resId.takeIf { it != 0 }
}

fun faceAssetPath(card: TarotCardModel, faceSkin: CardFaceSkin): String =
    "skins/${faceSkin.folder}/${normalizedFaceName(card)}.jpg"

fun backDrawableResId(context: Context, backStyle: CardBackStyle): Int? =
    context.resources.getIdentifier(backStyle.assetName.lowercase(), "drawable", context.packageName)
        .takeIf { it != 0 }

fun backAssetPath(backStyle: CardBackStyle): String =
    "skins/cardback/${backStyle.assetName.lowercase()}.jpg"
