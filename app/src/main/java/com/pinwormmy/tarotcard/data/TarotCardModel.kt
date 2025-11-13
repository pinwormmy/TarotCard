package com.pinwormmy.tarotcard.data

data class TarotCardModel(
    val id: String,
    val name: String,
    val arcana: String,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val description: String,
    val keywords: List<String>,
    val imageUrl: String? = null
)
