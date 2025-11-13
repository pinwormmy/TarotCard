package com.pinwormmy.tarotcard.ui

data class TarotCard(
    val id: Int,
    val name: String
    // 나중에 의미/이미지 리소스 등 추가 가능
)

val demoTarotCards = listOf(
    TarotCard(0, "The Fool"),
    TarotCard(1, "The Magician"),
    TarotCard(2, "The High Priestess"),
    TarotCard(3, "The Empress"),
    TarotCard(4, "The Emperor"),
    TarotCard(5, "The Hierophant"),
    TarotCard(6, "The Lovers"),
    TarotCard(7, "The Chariot"),
    TarotCard(8, "Strength"),
    TarotCard(9, "The Hermit"),
    // … 나중에 전부 채우면 됨
)
