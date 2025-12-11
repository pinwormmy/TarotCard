package com.pinwormmy.midoritarot.data

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertSame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotRepositoryTest {

    private lateinit var baseContext: Context
    private lateinit var originalLocales: LocaleListCompat
    private var originalDefaultLocale: Locale = Locale.getDefault()

    @Before
    fun setUp() {
        baseContext = ApplicationProvider.getApplicationContext()
        originalLocales = AppCompatDelegate.getApplicationLocales()
    }

    @After
    fun tearDown() {
        AppCompatDelegate.setApplicationLocales(originalLocales)
        Locale.setDefault(originalDefaultLocale)
        restoreBaseLocale()
    }

    @Test
    fun getCards_returnsCachedListForSameLocale() {
        val context = localizedContext("en")
        val repository = TarotRepository(context)

        val first = repository.getCards()
        val second = repository.getCards()

        assertSame(first, second)
    }

    @Test
    fun getCards_refreshesWhenLocaleChanges() {
        val englishRepo = TarotRepository(localizedContext("en"))
        val englishCards = englishRepo.getCards()

        val koreanRepo = TarotRepository(localizedContext("ko"))
        val koreanCards = koreanRepo.getCards()

        assertEquals("The Fool", englishCards.first().name)
        assertEquals("바보", koreanCards.first().name)
        assertNotEquals(englishCards.first().name, koreanCards.first().name)
    }

    private fun localizedContext(tag: String): Context {
        val locale = Locale.forLanguageTag(tag)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        Locale.setDefault(locale)
        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)
        return baseContext.createConfigurationContext(config)
    }

    private fun restoreBaseLocale() {
        AppCompatDelegate.setApplicationLocales(originalLocales)
        Locale.setDefault(originalDefaultLocale)
    }
}
