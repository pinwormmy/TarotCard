package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotRepositoryTest {

    private lateinit var repository: TarotRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = TarotRepository(context)
    }

    @Test
    fun getCards_cachesLastResolvedLocale() {
        val englishFirst = repository.getCards(locale = Locale.ENGLISH)
        val englishSecond = repository.getCards(locale = Locale.ENGLISH)
        assertSame(englishFirst, englishSecond)

        val japaneseFirst = repository.getCards(locale = Locale.JAPANESE)
        assertNotSame(englishFirst, japaneseFirst)

        val japaneseSecond = repository.getCards(locale = Locale.JAPANESE)
        assertSame(japaneseFirst, japaneseSecond)

        val englishThird = repository.getCards(locale = Locale.ENGLISH)
        assertNotSame(englishFirst, englishThird)
        assertNotSame(japaneseFirst, englishThird)
    }

    @Test
    fun getCard_usesProvidedLocale() {
        val english = repository.getCard("major_00", locale = Locale.ENGLISH)
        val japanese = repository.getCard("major_00", locale = Locale.JAPANESE)

        assertEquals("The Fool", english?.name)
        assertEquals("愚者", japanese?.name)
    }
}

