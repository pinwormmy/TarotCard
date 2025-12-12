package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.pinwormmy.midoritarot.assets.TarotJsonLoader
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import java.util.Locale

class TarotRepository(
    private val context: Context
) {
    private var cachedLocaleTag: String? = null
    private var cachedCards: List<TarotCardModel>? = null

    fun getCards(locale: Locale? = null): List<TarotCardModel> {
        val resolvedLocale = locale ?: currentLocale()
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

    private fun currentLocale(): Locale {
        val appLocale = AppCompatDelegate.getApplicationLocales().get(0)
        if (appLocale != null) return appLocale

        return context.resources.configuration.locales.get(0) ?: Locale.getDefault()
    }
}
