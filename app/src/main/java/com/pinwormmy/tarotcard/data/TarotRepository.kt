package com.pinwormmy.tarotcard.data

import android.content.Context
import com.pinwormmy.tarotcard.assets.TarotJsonLoader

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
