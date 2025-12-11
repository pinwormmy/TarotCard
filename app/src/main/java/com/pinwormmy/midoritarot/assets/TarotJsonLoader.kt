package com.pinwormmy.midoritarot.assets

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

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
    val resLocale = context.resources.configuration.locales.get(0)
    val appLocale = AppCompatDelegate.getApplicationLocales().get(0)
    return resLocale ?: appLocale ?: Locale.getDefault()
}

private fun localizedString(item: JSONObject, baseKey: String, locale: Locale): String {
    val lang = locale.language.lowercase()
    val localizedKey = "${baseKey}_${lang}"
    val localized = item.optString(localizedKey)
    val base = item.optString(baseKey)
    val english = item.optString("${baseKey}_en")
    val isSupported = lang == "ko" || lang == "en" || lang == "ja" || lang == "th"

    return when {
        localized.isNotBlank() -> localized
        lang == "ko" && base.isNotBlank() -> base
        english.isNotBlank() -> english
        base.isNotBlank() && isSupported -> base
        else -> base.ifBlank { english }
    }
}

private fun localizedKeywords(item: JSONObject, locale: Locale): List<String> {
    val lang = locale.language.lowercase()
    val localizedKey = "keywords_${lang}"
    val localized = item.optJSONArray(localizedKey)
    val base = item.optJSONArray("keywords")
    val english = item.optJSONArray("keywords_en")
    val isSupported = lang == "ko" || lang == "en" || lang == "ja" || lang == "th"

    val keywordsArray = when {
        localized != null -> localized
        lang == "ko" && base != null -> base
        english != null -> english
        base != null && isSupported -> base
        else -> english ?: base
    }
    return buildList {
        if (keywordsArray != null) {
            for (i in 0 until keywordsArray.length()) {
                add(keywordsArray.optString(i))
            }
        }
    }
}
