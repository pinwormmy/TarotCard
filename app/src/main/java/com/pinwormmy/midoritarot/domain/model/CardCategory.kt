package com.pinwormmy.midoritarot.domain.model

enum class CardCategory {
    MajorArcana,
    Wands,
    Cups,
    Swords,
    Pentacles
}

fun TarotCardModel.category(): CardCategory {
    val normalizedId = id.lowercase()
    return when {
        normalizedId.startsWith("major_") -> CardCategory.MajorArcana
        normalizedId.startsWith("wands_") -> CardCategory.Wands
        normalizedId.startsWith("cups_") -> CardCategory.Cups
        normalizedId.startsWith("swords_") -> CardCategory.Swords
        normalizedId.startsWith("pentacles_") || normalizedId.startsWith("pents_") -> CardCategory.Pentacles
        else -> {
            val value = arcana.lowercase()
            when {
                value.contains("wand") -> CardCategory.Wands
                value.contains("cup") -> CardCategory.Cups
                value.contains("sword") -> CardCategory.Swords
                value.contains("pentacle") || value.contains("coin") -> CardCategory.Pentacles
                else -> CardCategory.MajorArcana
            }
        }
    }
}

