package com.pinwormmy.midoritarot.assets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotDataArcanaFieldTest {
    @Test
    fun arcana_isMajorOrMinorAcrossLanguages() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val englishCards = TarotJsonLoader.load(context, locale = Locale.ENGLISH)
        assertEquals("Major Arcana", englishCards.first { it.id == "major_00" }.arcana)
        assertEquals("Minor Arcana", englishCards.first { it.id == "cups_01" }.arcana)

        val japaneseCards = TarotJsonLoader.load(context, locale = Locale.JAPANESE)
        assertEquals("大アルカナ", japaneseCards.first { it.id == "major_00" }.arcana)
        assertEquals("小アルカナ", japaneseCards.first { it.id == "cups_01" }.arcana)
    }
}
