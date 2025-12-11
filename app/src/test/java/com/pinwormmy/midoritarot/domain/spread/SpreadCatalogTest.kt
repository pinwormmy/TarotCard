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
        assertEquals("簡単な質問を書いてください（任意）", spread.questionPlaceholder.resolve(locale))
        val position = spread.positions.first()
        assertEquals("核心メッセージ", position.title.resolve(locale))
    }
}
