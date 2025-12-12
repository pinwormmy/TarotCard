package com.pinwormmy.midoritarot.assets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotDataLocalizationCompletenessTest {

    @Test
    fun load_english_hasEnglishArcanaAndNoHangulInNames() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val cards = TarotJsonLoader.load(context, locale = Locale.ENGLISH)

        assertEquals(78, cards.size)
        assertEquals(cards.size, cards.map { it.id }.toSet().size)
        assertTrue(cards.all { it.name.isNotBlank() })
        assertTrue(cards.all { it.arcana == "Major Arcana" || it.arcana == "Minor Arcana" })
        assertFalse(cards.any { containsHangul(it.name) })
    }

    @Test
    fun load_japanese_hasJapaneseArcanaAndNoHangulInNames() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val cards = TarotJsonLoader.load(context, locale = Locale.JAPANESE)

        assertEquals(78, cards.size)
        assertEquals(cards.size, cards.map { it.id }.toSet().size)
        assertTrue(cards.all { it.name.isNotBlank() })
        assertTrue(cards.all { it.arcana == "大アルカナ" || it.arcana == "小アルカナ" })
        assertFalse(cards.any { containsHangul(it.name) })
    }

    private fun containsHangul(text: String): Boolean =
        text.any { ch -> ch in '\uAC00'..'\uD7A3' }
}

