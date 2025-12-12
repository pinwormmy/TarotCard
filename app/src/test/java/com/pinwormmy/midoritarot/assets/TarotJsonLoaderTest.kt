package com.pinwormmy.midoritarot.assets

import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotJsonLoaderTest {

    @Test
    fun load_forUnsupportedLocale_fallsBackToEnglish() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val cards = TarotJsonLoader.load(context, locale = Locale.FRENCH)
        val fool = cards.first { it.id == "major_00" }

        assertEquals("The Fool", fool.name)
    }
}
