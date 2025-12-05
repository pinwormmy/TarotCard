package com.pinwormmy.midoritarot.assets

import android.content.Context
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import org.json.JSONArray

object TarotJsonLoader {
    fun load(
        context: Context,
        fileName: String = "tarot_data.json"
    ): List<TarotCardModel> {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val cards = mutableListOf<TarotCardModel>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val keywordsArray = item.optJSONArray("keywords")
            val keywords = buildList {
                if (keywordsArray != null) {
                    for (i in 0 until keywordsArray.length()) {
                        add(keywordsArray.optString(i))
                    }
                }
            }
            val uprightMeaning = item.optString("uprightMeaning")
                .takeIf { it.isNotBlank() }
                ?: item.optString("meaning")
            val reversedMeaning = item.optString("reversedMeaning")
                .takeIf { it.isNotBlank() }
                ?: "Blocked energy, delays, or the shadow of ${item.optString("name")}"
            val description = item.optString("description")
                .takeIf { it.isNotBlank() }
                ?: uprightMeaning

            cards += TarotCardModel(
                id = item.getString("id"),
                name = item.getString("name"),
                arcana = item.optString("arcana"),
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
