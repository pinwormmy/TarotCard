package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DailyCardRepositoryTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("daily_card_draw", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun getCardForToday_returnsExistingOnSecondCall() {
        val tarotRepository = TarotRepository(context)
        val clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))
        val repository = DailyCardRepository(context, tarotRepository, clock)

        val first = repository.getCardForToday()
        val second = repository.getCardForToday()

        assertFalse(first.isExisting)
        assertTrue(second.isExisting)
        assertEquals(first.card.id, second.card.id)
    }

    @Test
    fun getCardForToday_resetsWhenDateChanges() {
        val tarotRepository = TarotRepository(context)
        val day1 = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))
        val day2 = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneId.of("UTC"))

        DailyCardRepository(context, tarotRepository, day1).getCardForToday()
        val day2Result = DailyCardRepository(context, tarotRepository, day2).getCardForToday()

        assertFalse(day2Result.isExisting)
        val storedDate = context.getSharedPreferences("daily_card_draw", Context.MODE_PRIVATE)
            .getString("date", null)
        assertEquals("2025-01-02", storedDate)
    }
}

