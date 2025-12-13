package com.pinwormmy.midoritarot.core.localization

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalizedStringTest {
    @Test
    fun resolve_returnsBestMatch() {
        val text = LocalizedString(
            ko = "Korean",
            en = "English",
            ja = "Japanese",
            th = "Thai"
        )

        assertEquals("Korean", text.resolve(Locale("ko")))
        assertEquals("English", text.resolve(Locale("en")))
        assertEquals("Japanese", text.resolve(Locale("ja")))
        assertEquals("Thai", text.resolve(Locale("th")))
        assertEquals("English", text.resolve(Locale("fr")))
    }

    @Test
    fun resolve_fallsBackToKoreanWhenEnglishMissing() {
        val text = LocalizedString(
            ko = "Korean",
            en = null,
            ja = "Japanese"
        )

        assertEquals("Korean", text.resolve(Locale("en")))
        assertEquals("Japanese", text.resolve(Locale("fr")))
    }
}

