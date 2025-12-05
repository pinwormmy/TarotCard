package com.pinwormmy.midoritarot.data

import android.content.Context
import com.pinwormmy.midoritarot.assets.TarotJsonLoader
import com.pinwormmy.midoritarot.domain.model.TarotCardModel

class TarotRepository(
    private val context: Context
) {
    private val allCards: List<TarotCardModel> by lazy {
        TarotJsonLoader.load(context)
    }

    fun getCards(): List<TarotCardModel> = allCards

    fun getCard(cardId: String?): TarotCardModel? =
        allCards.firstOrNull { it.id == cardId }
}
