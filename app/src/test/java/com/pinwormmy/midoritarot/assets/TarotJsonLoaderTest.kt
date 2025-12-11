package com.pinwormmy.midoritarot.assets

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotJsonLoaderTest {

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
    fun load_respectsEnglishLocale() {
        val context = localizedContext("en")

        val cards = TarotJsonLoader.load(context)

        assertEquals(78, cards.size)
        val first = cards.first()
        assertEquals("The Fool", first.name)
        assertTrue(first.keywords.contains("beginnings"))
    }

    @Test
    fun load_respectsKoreanLocale() {
        val context = localizedContext("ko")

        val cards = TarotJsonLoader.load(context)

        val first = cards.first()
        assertEquals("바보", first.name)
        assertTrue(first.keywords.contains("시작"))
    }

    @Test
    fun load_providesReversedMeaningFallback() {
        val context = localizedContext("en")
        val cards = TarotJsonLoader.load(context)
        val sample = cards.first { it.reversedMeaning.isNotBlank() }

        assertFalse(sample.reversedMeaning.isBlank())
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
