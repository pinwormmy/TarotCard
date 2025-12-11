package com.pinwormmy.midoritarot.assets

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotJsonLoaderTest {

    private lateinit var originalLocales: LocaleListCompat
    private var originalDefault: Locale = Locale.getDefault()

    @Before
    fun setUp() {
        originalLocales = AppCompatDelegate.getApplicationLocales()
    }

    @After
    fun tearDown() {
        AppCompatDelegate.setApplicationLocales(originalLocales)
        Locale.setDefault(originalDefault)
    }

    @Test
    fun load_forUnsupportedLocale_fallsBackToEnglish() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("fr"))
        Locale.setDefault(Locale.FRANCE)

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val cards = TarotJsonLoader.load(context)
        val fool = cards.first { it.id == "major_00" }

        assertEquals("The Fool", fool.name)
    }
}
