package com.pinwormmy.midoritarot.assets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotJsonLoaderLocaleTest {
    @Test
    fun load_usesProvidedLocaleWhenPresent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val card = TarotJsonLoader.load(context, locale = Locale.ENGLISH).first { it.id == "major_00" }
        assertEquals("The Fool", card.name)
    }
}
