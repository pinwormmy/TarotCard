package com.pinwormmy.midoritarot.data

import android.content.Context
import com.pinwormmy.midoritarot.assets.TarotJsonLoader
import com.pinwormmy.midoritarot.core.localization.currentAppLocale
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import java.util.Locale

class TarotRepository(
    private val context: Context
) {
    private var cachedLocaleTag: String? = null
    private var cachedCards: List<TarotCardModel>? = null

    fun getCards(locale: Locale? = null): List<TarotCardModel> {
        val resolvedLocale = locale ?: currentAppLocale(context)
        val currentTag = resolvedLocale.toLanguageTag()
        val cached = cachedCards
        if (cached != null && cachedLocaleTag == currentTag) return cached

        val fresh = TarotJsonLoader.load(context, locale = resolvedLocale)
        cachedCards = fresh
        cachedLocaleTag = currentTag
        return fresh
    }

    fun getCard(cardId: String?, locale: Locale? = null): TarotCardModel? =
        getCards(locale).firstOrNull { it.id == cardId }
}
