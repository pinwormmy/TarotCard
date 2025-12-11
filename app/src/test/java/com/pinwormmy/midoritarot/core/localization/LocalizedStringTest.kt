package com.pinwormmy.midoritarot.core.localization

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalizedStringTest {

    private lateinit var originalLocales: LocaleListCompat
    private var originalDefaultLocale: Locale = Locale.getDefault()

    @Before
    fun setUp() {
        originalLocales = AppCompatDelegate.getApplicationLocales()
    }

    @After
    fun tearDown() {
        AppCompatDelegate.setApplicationLocales(originalLocales)
        Locale.setDefault(originalDefaultLocale)
    }

    @Test
    fun resolve_usesAppLocaleWhenProvided() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ja"))
        Locale.setDefault(Locale.JAPAN)

        val target = LocalizedString(
            ko = "한국어",
            en = "English",
            ja = "日本語"
        )

        val resolved = target.resolve()

        assertEquals("日本語", resolved)
    }
}
