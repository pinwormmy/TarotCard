package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pinwormmy.midoritarot.ui.state.AppLanguage
import com.pinwormmy.midoritarot.ui.state.CardBackStyle
import com.pinwormmy.midoritarot.ui.state.CardFaceSkin
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        context.getSharedPreferences("tarot_settings", Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences("daily_card_notification", Context.MODE_PRIVATE).edit().clear().apply()
    }

    @After
    fun tearDown() {
        context.getSharedPreferences("tarot_settings", Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences("daily_card_notification", Context.MODE_PRIVATE).edit().clear().apply()
    }

    @Test
    fun saveAndLoad_persistsJapaneseLanguage() {
        val initial = repository.load()
        val updated = initial.copy(language = AppLanguage.Japanese)

        repository.save(updated)
        val loaded = repository.load()

        assertEquals(AppLanguage.Japanese, loaded.language)
    }

    @Test
    fun load_whenPrefsEmpty_returnsDefaults() {
        val loaded = repository.load()

        assertEquals(AppLanguage.System, loaded.language)
        assertTrue(loaded.hapticsEnabled)
        assertTrue(loaded.useReversedCards)
        assertFalse(loaded.dailyCardNotification)
        assertEquals(LocalTime.of(9, 0), loaded.dailyCardTime)
        assertEquals(CardBackStyle.Byzantine, loaded.cardBackStyle)
        assertEquals(CardFaceSkin.Animation, loaded.cardFaceSkin)
    }

    @Test
    fun load_whenStoredValuesInvalid_fallsBackToDefaults() {
        context.getSharedPreferences("tarot_settings", Context.MODE_PRIVATE).edit()
            .putString("card_back", "InvalidBack")
            .putString("card_face", "InvalidFace")
            .putString("daily_time", "not_a_time")
            .apply()

        val loaded = repository.load()

        assertEquals(CardBackStyle.Byzantine, loaded.cardBackStyle)
        assertEquals(CardFaceSkin.Animation, loaded.cardFaceSkin)
        assertEquals(LocalTime.of(9, 0), loaded.dailyCardTime)
    }

    @Test
    fun saveAndLoad_persistsMultipleSettings() {
        val initial = repository.load()
        val updated = initial.copy(
            dailyCardNotification = true,
            dailyCardTime = LocalTime.of(7, 30),
            hapticsEnabled = false,
            useReversedCards = false,
            cardBackStyle = CardBackStyle.Persia,
            language = AppLanguage.English
        )

        repository.save(updated)
        val loaded = repository.load()

        assertTrue(loaded.dailyCardNotification)
        assertEquals(LocalTime.of(7, 30), loaded.dailyCardTime)
        assertFalse(loaded.hapticsEnabled)
        assertFalse(loaded.useReversedCards)
        assertEquals(CardBackStyle.Persia, loaded.cardBackStyle)
        assertEquals(AppLanguage.English, loaded.language)
    }
}
