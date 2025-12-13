package com.pinwormmy.midoritarot.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CardCategoryTest {
    @Test
    fun category_usesIdPrefixWhenPresent() {
        fun card(id: String) = TarotCardModel(
            id = id,
            name = "Dummy",
            arcana = "Dummy",
            uprightMeaning = "",
            reversedMeaning = "",
            description = "",
            keywords = emptyList()
        )

        assertEquals(CardCategory.MajorArcana, card("major_00").category())
        assertEquals(CardCategory.Wands, card("wands_01").category())
        assertEquals(CardCategory.Cups, card("cups_02").category())
        assertEquals(CardCategory.Swords, card("swords_03").category())
        assertEquals(CardCategory.Pentacles, card("pentacles_04").category())
        assertEquals(CardCategory.Pentacles, card("pents_05").category())
    }

    @Test
    fun category_fallsBackToArcanaText() {
        val card = TarotCardModel(
            id = "unknown",
            name = "Dummy",
            arcana = "Coins",
            uprightMeaning = "",
            reversedMeaning = "",
            description = "",
            keywords = emptyList()
        )

        assertEquals(CardCategory.Pentacles, card.category())
    }
}

