package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pinwormmy.midoritarot.domain.spread.SpreadType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DrawHistoryRepositoryTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("draw_history", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun recordReading_ignoresEmptyCards() {
        val repository = DrawHistoryRepository(context)
        repository.recordReading(
            spreadType = SpreadType.PastPresentFuture,
            questionText = "test",
            cards = emptyList(),
            timestampEpochMillis = 1L,
        )

        assertTrue(repository.entries.value.isEmpty())
    }

    @Test
    fun recordReading_keepsMostRecentTenInOrder() {
        val repository = DrawHistoryRepository(context)

        repeat(12) { index ->
            repository.recordReading(
                spreadType = SpreadType.PastPresentFuture,
                questionText = "q$index",
                cards = listOf(
                    DrawHistoryCard(
                        slotId = "slot$index",
                        cardId = "card$index",
                        isReversed = index % 2 == 0,
                    )
                ),
                timestampEpochMillis = 1_000L + index,
            )
        }

        val entries = repository.entries.value
        assertEquals(10, entries.size)
        assertEquals("q11", entries.first().questionText)
        assertEquals("q2", entries.last().questionText)
    }

    @Test
    fun loadFromPrefs_returnsEmptyOnCorruptJson() {
        context.getSharedPreferences("draw_history", Context.MODE_PRIVATE)
            .edit()
            .putString("entries", "{not_json")
            .commit()

        val repository = DrawHistoryRepository(context)
        assertTrue(repository.entries.value.isEmpty())
    }
}

