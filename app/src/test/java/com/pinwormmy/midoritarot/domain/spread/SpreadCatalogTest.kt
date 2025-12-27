package com.pinwormmy.midoritarot.domain.spread

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SpreadCatalogTest {

    @Test
    fun oneCard_titles_resolveJapanese() {
        val locale = Locale.JAPANESE
        val spread = SpreadCatalog.find(SpreadType.OneCard)

        assertEquals("ワンカード", spread.title.resolve(locale))
        assertEquals("（任意）質問を入力してください", spread.questionPlaceholder.resolve(locale))
        val position = spread.positions.first()
        assertEquals("核心メッセージ", position.title.resolve(locale))
    }

    @Test
    fun dailyCard_hasSingleSlotAndKoreanTitle() {
        val spread = SpreadCatalog.find(SpreadType.DailyCard)

        assertEquals("오늘 하루 운세 카드", spread.title.resolve(Locale.KOREAN))
        assertEquals(1, spread.positions.size)
        assertEquals("daily_card", spread.positions.first().slot.id)
    }
}
