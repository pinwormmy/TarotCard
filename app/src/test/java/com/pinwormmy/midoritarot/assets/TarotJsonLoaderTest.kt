package com.pinwormmy.midoritarot.assets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotJsonLoaderTest {
    @Test
    fun load_returnsLocalizedEnglishCards() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val cards = TarotJsonLoader.load(context, locale = Locale("en"))
        assertEquals(78, cards.size)

        val fool = cards.firstOrNull { it.id == "major_00" }
        assertNotNull(fool)
        assertEquals("The Fool", fool?.name)
        assertEquals("Major Arcana", fool?.arcana)
        assertTrue(fool?.keywords?.contains("beginnings") == true)
    }

    @Test
    fun load_returnsLocalizedKoreanCards() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val cards = TarotJsonLoader.load(context, locale = Locale("ko"))
        val fool = cards.firstOrNull { it.id == "major_00" }

        assertNotNull(fool)
        assertEquals("바보", fool?.name)
        assertEquals("메이저 아르카나", fool?.arcana)
    }

    @Test
    fun load_fallsBackToEnglishForUnsupportedLocale() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val cards = TarotJsonLoader.load(context, locale = Locale("fr"))
        val fool = cards.firstOrNull { it.id == "major_00" }

        assertNotNull(fool)
        assertEquals("The Fool", fool?.name)
    }
}

