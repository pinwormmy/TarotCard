package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.core.content.edit
import com.pinwormmy.midoritarot.domain.spread.SpreadType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private const val PREF_NAME = "draw_history"
private const val KEY_ENTRIES = "entries"
private const val MAX_ENTRIES = 10

data class DrawHistoryEntry(
    val id: String,
    val timestampEpochMillis: Long,
    val spreadType: SpreadType,
    val questionText: String,
    val cards: List<DrawHistoryCard>,
)

data class DrawHistoryCard(
    val slotId: String,
    val cardId: String,
    val isReversed: Boolean,
)

class DrawHistoryRepository(
    context: Context,
) {
    private val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val _entries = MutableStateFlow(loadFromPrefs())

    val entries: StateFlow<List<DrawHistoryEntry>> = _entries.asStateFlow()

    fun recordReading(
        spreadType: SpreadType,
        questionText: String,
        cards: List<DrawHistoryCard>,
        timestampEpochMillis: Long = System.currentTimeMillis(),
    ) {
        if (cards.isEmpty()) return

        val entry = DrawHistoryEntry(
            id = UUID.randomUUID().toString(),
            timestampEpochMillis = timestampEpochMillis,
            spreadType = spreadType,
            questionText = questionText,
            cards = cards,
        )

        val updated = buildList {
            add(entry)
            addAll(_entries.value)
        }.take(MAX_ENTRIES)

        saveToPrefs(updated)
        _entries.value = updated
    }

    private fun loadFromPrefs(): List<DrawHistoryEntry> {
        val raw = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val item = array.optJSONObject(i) ?: continue
                    parseEntry(item)?.let(::add)
                }
            }
        }.getOrElse { emptyList() }
    }

    private fun parseEntry(item: JSONObject): DrawHistoryEntry? {
        val id = item.optString("id").takeIf { it.isNotBlank() } ?: return null
        val timestamp = item.optLong("timestampEpochMillis", -1L)
        if (timestamp <= 0L) return null
        val spreadTypeName = item.optString("spreadType").takeIf { it.isNotBlank() } ?: return null
        val spreadType = SpreadType.entries.firstOrNull { it.name == spreadTypeName } ?: return null
        val questionText = item.optString("questionText", "")
        val cardsArray = item.optJSONArray("cards") ?: return null
        val cards = buildList {
            for (i in 0 until cardsArray.length()) {
                val cardObj = cardsArray.optJSONObject(i) ?: continue
                val slotId = cardObj.optString("slotId").takeIf { it.isNotBlank() } ?: continue
                val cardId = cardObj.optString("cardId").takeIf { it.isNotBlank() } ?: continue
                val isReversed = cardObj.optBoolean("isReversed", false)
                add(
                    DrawHistoryCard(
                        slotId = slotId,
                        cardId = cardId,
                        isReversed = isReversed,
                    )
                )
            }
        }

        if (cards.isEmpty()) return null
        return DrawHistoryEntry(
            id = id,
            timestampEpochMillis = timestamp,
            spreadType = spreadType,
            questionText = questionText,
            cards = cards,
        )
    }

    private fun saveToPrefs(entries: List<DrawHistoryEntry>) {
        val payload = JSONArray()
        entries.forEach { entry ->
            payload.put(
                JSONObject()
                    .put("id", entry.id)
                    .put("timestampEpochMillis", entry.timestampEpochMillis)
                    .put("spreadType", entry.spreadType.name)
                    .put("questionText", entry.questionText)
                    .put(
                        "cards",
                        JSONArray().apply {
                            entry.cards.forEach { card ->
                                put(
                                    JSONObject()
                                        .put("slotId", card.slotId)
                                        .put("cardId", card.cardId)
                                        .put("isReversed", card.isReversed)
                                )
                            }
                        }
                    )
            )
        }
        prefs.edit { putString(KEY_ENTRIES, payload.toString()) }
    }
}
