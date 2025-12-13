package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotRepositoryTest {
    @Test
    fun getCards_cachesByLocaleTag() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = TarotRepository(context)

        val english = repository.getCards(locale = Locale("en"))
        val englishAgain = repository.getCards(locale = Locale("en"))
        val japanese = repository.getCards(locale = Locale("ja"))

        assertSame(english, englishAgain)
        assertNotSame(english, japanese)
        assertEquals(78, english.size)
    }

    @Test
    fun getCard_returnsCardWhenPresent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = TarotRepository(context)

        val card = repository.getCard("major_00", locale = Locale("en"))
        assertNotNull(card)
        assertEquals("The Fool", card?.name)
    }
}

