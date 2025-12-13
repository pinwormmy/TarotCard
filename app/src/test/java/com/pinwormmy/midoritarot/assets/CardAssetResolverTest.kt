package com.pinwormmy.midoritarot.assets

import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import org.junit.Assert.assertEquals
import org.junit.Test

class CardAssetResolverTest {
    @Test
    fun normalizedFaceName_returnsIdWhenImageUrlMissing() {
        val card = TarotCardModel(
            id = "major_00",
            name = "Dummy",
            arcana = "Major Arcana",
            uprightMeaning = "",
            reversedMeaning = "",
            description = "",
            keywords = emptyList(),
            imageUrl = null
        )

        assertEquals("major_00", normalizedFaceName(card))
    }

    @Test
    fun normalizedFaceName_stripsExtensionAndQuery() {
        val card = TarotCardModel(
            id = "major_00",
            name = "Dummy",
            arcana = "Major Arcana",
            uprightMeaning = "",
            reversedMeaning = "",
            description = "",
            keywords = emptyList(),
            imageUrl = "https://example.com/skins/animation/tarot00.jpg?ver=1"
        )

        assertEquals("tarot00", normalizedFaceName(card))
    }

    @Test
    fun normalizedFaceName_stripsWindowsPathAndExtension() {
        val card = TarotCardModel(
            id = "major_00",
            name = "Dummy",
            arcana = "Major Arcana",
            uprightMeaning = "",
            reversedMeaning = "",
            description = "",
            keywords = emptyList(),
            imageUrl = "C:\\images\\Tarot00.JPG"
        )

        assertEquals("tarot00", normalizedFaceName(card))
    }
}

