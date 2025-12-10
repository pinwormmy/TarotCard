package com.pinwormmy.midoritarot.domain.spread

import androidx.compose.runtime.Immutable

@JvmInline
value class SpreadSlot(val id: String)

enum class SpreadType {
    OneCard,
    PastPresentFuture,
    EnergyAdvice,
    PathForward,
    CelticCross
}

@Immutable
data class SpreadPlacement(
    val column: Int,
    val row: Int,
    val rotationDegrees: Float = 0f,
    val zIndex: Float = 0f
)

@Immutable
data class SpreadLayout(
    val columns: Int,
    val rows: Int
)

@Immutable
data class SpreadPosition(
    val slot: SpreadSlot,
    val title: LocalizedText,
    val description: LocalizedText,
    val order: Int,
    val placement: SpreadPlacement
)

@Immutable
data class SpreadDefinition(
    val type: SpreadType,
    val title: LocalizedText,
    val description: LocalizedText,
    val questionPlaceholder: LocalizedText,
    val layout: SpreadLayout,
    val positions: List<SpreadPosition>,
    val defaultUseReversed: Boolean = true
)

object SpreadCatalog {
    private val oneCard = SpreadDefinition(
        type = SpreadType.OneCard,
        title = LocalizedText(
            ko = "원카드",
            en = "One Card"
        ),
        description = LocalizedText(
            ko = "오늘 필요한 핵심 메시지 한 장으로 빠르게 리딩합니다.",
            en = "A single card to receive the core message you need today."
        ),
        questionPlaceholder = LocalizedText(
            ko = "간단한 질문을 남겨보세요 (선택)",
            en = "Add a quick question (optional)"
        ),
        layout = SpreadLayout(columns = 1, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("one_focus"),
                title = LocalizedText(ko = "핵심 메시지", en = "Core Message"),
                description = LocalizedText(
                    ko = "현재 상황을 가장 잘 비추는 카드",
                    en = "The card that best reflects your situation now."
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            )
        )
    )

    private val pastPresentFuture = SpreadDefinition(
        type = SpreadType.PastPresentFuture,
        title = LocalizedText(ko = "쓰리카드", en = "Three Card"),
        description = LocalizedText(
            ko = "3장의 카드를 순서대로 배열해 상황을 간단히 읽습니다.",
            en = "Read the situation with three cards laid out in order."
        ),
        questionPlaceholder = LocalizedText(
            ko = "궁금한 상황을 짧게 적어주세요 (선택)",
            en = "Write your question briefly (optional)"
        ),
        layout = SpreadLayout(columns = 3, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("ppf_past"),
                title = LocalizedText(ko = "첫번째 카드", en = "First Card"),
                description = LocalizedText(
                    ko = "첫 흐름 또는 배경",
                    en = "Initial flow or background."
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("ppf_present"),
                title = LocalizedText(ko = "두번째 카드", en = "Second Card"),
                description = LocalizedText(
                    ko = "현재 핵심 메시지",
                    en = "The core message of the present."
                ),
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("ppf_future"),
                title = LocalizedText(ko = "세번째 카드", en = "Third Card"),
                description = LocalizedText(
                    ko = "다음으로 이어질 가능성",
                    en = "What is likely to unfold next."
                ),
                order = 3,
                placement = SpreadPlacement(column = 2, row = 0)
            )
        )
    )

    private val energyAdvice = SpreadDefinition(
        type = SpreadType.EnergyAdvice,
        title = LocalizedText(ko = "투카드", en = "Two Card"),
        description = LocalizedText(
            ko = "2장의 카드를 나란히 펼쳐 빠르게 핵심을 봅니다.",
            en = "Two side‑by‑side cards to see the core quickly."
        ),
        questionPlaceholder = LocalizedText(
            ko = "집중하려는 주제를 적어주세요 (선택)",
            en = "Write the topic you’re focusing on (optional)"
        ),
        layout = SpreadLayout(columns = 2, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("energy_now"),
                title = LocalizedText(ko = "첫번째 카드", en = "First Card"),
                description = LocalizedText(
                    ko = "상황 또는 핵심 포인트",
                    en = "Current situation or main point."
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("energy_advice"),
                title = LocalizedText(ko = "두번째 카드", en = "Second Card"),
                description = LocalizedText(
                    ko = "보완 또는 조언",
                    en = "Complementary energy or advice."
                ),
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            )
        )
    )

    private val pathForward = SpreadDefinition(
        type = SpreadType.PathForward,
        title = LocalizedText(ko = "포카드", en = "Four Card"),
        description = LocalizedText(
            ko = "4장의 카드로 상황과 다음 단계를 단계별로 훑습니다.",
            en = "Four cards to scan the situation and next steps."
        ),
        questionPlaceholder = LocalizedText(
            ko = "보고 싶은 상황을 적어주세요 (선택)",
            en = "Describe the situation you want to explore (optional)"
        ),
        layout = SpreadLayout(columns = 4, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("path_now"),
                title = LocalizedText(ko = "첫번째 카드", en = "First Card"),
                description = LocalizedText(
                    ko = "출발점 혹은 현재",
                    en = "Starting point or present."
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_challenge"),
                title = LocalizedText(ko = "두번째 카드", en = "Second Card"),
                description = LocalizedText(
                    ko = "지나야 할 요소",
                    en = "The obstacle to move through."
                ),
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_guidance"),
                title = LocalizedText(ko = "세번째 카드", en = "Third Card"),
                description = LocalizedText(
                    ko = "도움이 되는 관점",
                    en = "Perspective that helps."
                ),
                order = 3,
                placement = SpreadPlacement(column = 2, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_outcome"),
                title = LocalizedText(ko = "네번째 카드", en = "Fourth Card"),
                description = LocalizedText(
                    ko = "이어질 결과/다음 단계",
                    en = "Likely outcome or next step."
                ),
                order = 4,
                placement = SpreadPlacement(column = 3, row = 0)
            )
        )
    )

    private val celticCross = SpreadDefinition(
        type = SpreadType.CelticCross,
        title = LocalizedText(ko = "켈틱 크로스", en = "Celtic Cross"),
        description = LocalizedText(
            ko = "10장의 전통 스프레드로 상황을 다각도로 조망합니다.",
            en = "A classic 10-card spread to view the situation from many angles."
        ),
        questionPlaceholder = LocalizedText(
            ko = "깊이 들여다보고 싶은 질문을 적어보세요 (선택)",
            en = "Write the deep question you want to explore (optional)"
        ),
        layout = SpreadLayout(columns = 4, rows = 4),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("celtic_present"),
                title = LocalizedText(ko = "현재 상황", en = "Present Situation"),
                description = LocalizedText(
                    ko = "리딩의 중심 주제",
                    en = "Core theme of the reading."
                ),
                order = 1,
                placement = SpreadPlacement(column = 1, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_crossing"),
                title = LocalizedText(ko = "교차 에너지", en = "Crossing Energy"),
                description = LocalizedText(
                    ko = "도전 혹은 보완 요소",
                    en = "Challenge or assisting factor."
                ),
                order = 2,
                placement = SpreadPlacement(column = 1, row = 1, rotationDegrees = 90f, zIndex = 1f)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_foundation"),
                title = LocalizedText(ko = "근본 원인", en = "Foundation"),
                description = LocalizedText(
                    ko = "숨은 뿌리, 무의식",
                    en = "Hidden roots or subconscious."
                ),
                order = 3,
                placement = SpreadPlacement(column = 1, row = 2)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_past"),
                title = LocalizedText(ko = "최근 과거", en = "Recent Past"),
                description = LocalizedText(
                    ko = "지나간 영향",
                    en = "Influence that has passed."
                ),
                order = 4,
                placement = SpreadPlacement(column = 0, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_conscious"),
                title = LocalizedText(ko = "의식 / 가능성", en = "Conscious / Potential"),
                description = LocalizedText(
                    ko = "상황이 향하는 상단",
                    en = "Where things are consciously headed."
                ),
                order = 5,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_near_future"),
                title = LocalizedText(ko = "다가올 일", en = "Near Future"),
                description = LocalizedText(
                    ko = "머지않아 다가올 기류",
                    en = "Energy arriving soon."
                ),
                order = 6,
                placement = SpreadPlacement(column = 2, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_self"),
                title = LocalizedText(ko = "나 자신", en = "Self"),
                description = LocalizedText(
                    ko = "질문자 상태",
                    en = "State of the querent."
                ),
                order = 7,
                placement = SpreadPlacement(column = 3, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_environment"),
                title = LocalizedText(ko = "환경 / 타인", en = "Environment / Others"),
                description = LocalizedText(
                    ko = "주변에서 오는 영향",
                    en = "Influences from surroundings."
                ),
                order = 8,
                placement = SpreadPlacement(column = 3, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_hopes"),
                title = LocalizedText(ko = "희망과 두려움", en = "Hopes and Fears"),
                description = LocalizedText(
                    ko = "마음이 품은 양면",
                    en = "What the heart hopes for and fears."
                ),
                order = 9,
                placement = SpreadPlacement(column = 3, row = 2)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_outcome"),
                title = LocalizedText(ko = "잠재적 결과", en = "Potential Outcome"),
                description = LocalizedText(
                    ko = "전체 흐름이 닿을 곳",
                    en = "Where the overall flow may land."
                ),
                order = 10,
                placement = SpreadPlacement(column = 3, row = 3)
            )
        )
    )

    private val allDefinitions = listOf(
        oneCard,           // 1장
        energyAdvice,      // 2장
        pastPresentFuture, // 3장
        pathForward,       // 4장
        celticCross        // 10장
    )

    val default: SpreadDefinition = pastPresentFuture

    val all: List<SpreadDefinition> = allDefinitions

    fun find(type: SpreadType): SpreadDefinition =
        allDefinitions.firstOrNull { it.type == type } ?: default
}
