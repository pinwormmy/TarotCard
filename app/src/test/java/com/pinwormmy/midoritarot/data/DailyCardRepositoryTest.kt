package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DailyCardRepositoryTest {

    private lateinit var context: Context
    private lateinit var tarotRepository: TarotRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        tarotRepository = TarotRepository(context)
        clearPrefs()
    }

    @After
    fun tearDown() {
        clearPrefs()
    }

    @Test
    fun getCardForToday_returnsExistingCardWhenStoredForToday() {
        val zone = ZoneId.of("UTC")
        val clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), zone)
        val today = LocalDate.now(clock)

        prefs().edit()
            .putString("date", today.toString())
            .putString("card_id", "major_00")
            .apply()

        val repository = DailyCardRepository(context, tarotRepository, clock)
        val result = repository.getCardForToday()

        assertTrue(result.isExisting)
        assertEquals("major_00", result.card.id)
    }

    @Test
    fun getCardForToday_drawsNewCardWhenStoredIdMissing() {
        val zone = ZoneId.of("UTC")
        val clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), zone)
        val today = LocalDate.now(clock)

        prefs().edit()
            .putString("date", today.toString())
            .putString("card_id", "missing_card_id")
            .apply()

        val repository = DailyCardRepository(context, tarotRepository, clock)
        val result = repository.getCardForToday()

        assertFalse(result.isExisting)
        assertNotNull(result.card.id)

        val storedDate = prefs().getString("date", null)
        val storedCardId = prefs().getString("card_id", null)

        assertEquals(today.toString(), storedDate)
        assertEquals(result.card.id, storedCardId)
    }

    @Test
    fun getCardForToday_drawsNewCardWhenStoredDateIsDifferent() {
        val zone = ZoneId.of("UTC")
        val clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), zone)
        val today = LocalDate.now(clock)
        val yesterday = today.minusDays(1)

        prefs().edit()
            .putString("date", yesterday.toString())
            .putString("card_id", "major_00")
            .apply()

        val repository = DailyCardRepository(context, tarotRepository, clock)
        val result = repository.getCardForToday()

        assertFalse(result.isExisting)

        val storedDate = prefs().getString("date", null)
        assertEquals(today.toString(), storedDate)
    }

    private fun prefs() =
        context.getSharedPreferences("daily_card_draw", Context.MODE_PRIVATE)

    private fun clearPrefs() {
        prefs().edit().clear().apply()
    }
}

