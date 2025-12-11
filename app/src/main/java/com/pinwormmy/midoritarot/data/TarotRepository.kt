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

    fun getCards(): List<TarotCardModel> {
        val locale = currentLocale()
        val currentTag = locale.toLanguageTag()
        val cached = cachedCards
        if (cached != null && cachedLocaleTag == currentTag) return cached

        val fresh = TarotJsonLoader.load(context)
        cachedCards = fresh
        cachedLocaleTag = currentTag
        return fresh
    }

    fun getCard(cardId: String?): TarotCardModel? =
        getCards().firstOrNull { it.id == cardId }

    private fun currentLocale(): Locale {
        val resLocale = context.resources.configuration.locales.get(0)
        val appLocale = AppCompatDelegate.getApplicationLocales().get(0)
        return resLocale ?: appLocale ?: Locale.getDefault()
    }
}
