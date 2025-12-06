package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.core.content.edit
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import java.time.Clock
import java.time.LocalDate

private const val PREF_NAME = "daily_card_draw"
private const val KEY_DATE = "date"
private const val KEY_CARD_ID = "card_id"

data class DailyCardResult(
    val card: TarotCardModel,
    val isExisting: Boolean
)

class DailyCardRepository(
    context: Context,
    private val tarotRepository: TarotRepository,
    private val clock: Clock = Clock.systemDefaultZone()
) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getCardForToday(): DailyCardResult {
        val today = LocalDate.now(clock)
        val storedDate = prefs.getString(KEY_DATE, null)
        val storedCardId = prefs.getString(KEY_CARD_ID, null)

        if (storedDate != null && storedCardId != null) {
            val savedDate = runCatching { LocalDate.parse(storedDate) }.getOrNull()
            if (savedDate == today) {
                tarotRepository.getCard(storedCardId)?.let {
                    return DailyCardResult(
                        card = it,
                        isExisting = true
                    )
                }
            }
        }

        val newCard = tarotRepository.getCards().random()
        prefs.edit {
            putString(KEY_DATE, today.toString())
            putString(KEY_CARD_ID, newCard.id)
        }
        return DailyCardResult(
            card = newCard,
            isExisting = false
        )
    }
}
