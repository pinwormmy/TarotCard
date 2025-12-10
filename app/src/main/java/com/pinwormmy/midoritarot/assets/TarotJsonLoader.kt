package com.pinwormmy.midoritarot.assets

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

object TarotJsonLoader {
    fun load(
        context: Context,
        fileName: String = "tarot_data.json"
    ): List<TarotCardModel> {
        val locale = currentLocale(context)
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val cards = mutableListOf<TarotCardModel>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val keywords = localizedKeywords(item, locale)
            val name = localizedString(item, "name", locale)
            val arcana = localizedString(item, "arcana", locale)
            val uprightMeaning = localizedString(item, "uprightMeaning", locale)
                .ifBlank { item.optString("meaning") }
            val reversedMeaning = localizedString(item, "reversedMeaning", locale)
                .ifBlank { "Blocked energy, delays, or the shadow of $name" }
            val description = localizedString(item, "description", locale)
                .ifBlank { uprightMeaning }

            cards += TarotCardModel(
                id = item.getString("id"),
                name = name,
                arcana = arcana,
                uprightMeaning = uprightMeaning,
                reversedMeaning = reversedMeaning,
                description = description,
                keywords = keywords,
                imageUrl = item.optString("imageUrl").takeIf { it.isNotBlank() }
            )
        }
        return cards
    }
}

private fun currentLocale(context: Context): Locale {
    val appLocales: LocaleListCompat = AppCompatDelegate.getApplicationLocales()
    val locale = appLocales.get(0)
        ?: context.resources.configuration.locales.get(0)
        ?: Locale.getDefault()
    return locale
}

private fun localizedString(item: JSONObject, baseKey: String, locale: Locale): String {
    val lang = locale.language.lowercase()
    val localizedKey = "${baseKey}_${lang}"
    return item.optString(localizedKey)
        .takeIf { it.isNotBlank() }
        ?: item.optString(baseKey)
}

private fun localizedKeywords(item: JSONObject, locale: Locale): List<String> {
    val lang = locale.language.lowercase()
    val localizedKey = "keywords_${lang}"
    val keywordsArray = item.optJSONArray(localizedKey) ?: item.optJSONArray("keywords")
    return buildList {
        if (keywordsArray != null) {
            for (i in 0 until keywordsArray.length()) {
                add(keywordsArray.optString(i))
            }
        }
    }
}
