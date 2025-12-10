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
    val title: String,
    val description: String,
    val order: Int,
    val placement: SpreadPlacement
)

@Immutable
data class SpreadDefinition(
    val type: SpreadType,
    val title: String,
    val description: String,
    val questionPlaceholder: String,
    val layout: SpreadLayout,
    val positions: List<SpreadPosition>,
    val defaultUseReversed: Boolean = true
)

object SpreadCatalog {
    private val oneCard = SpreadDefinition(
        type = SpreadType.OneCard,
        title = "원카드",
        description = "오늘 필요한 핵심 메시지 한 장으로 빠르게 리딩합니다.",
        questionPlaceholder = "간단한 질문을 남겨보세요 (선택)",
        layout = SpreadLayout(columns = 1, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("one_focus"),
                title = "핵심 메시지",
                description = "현재 상황을 가장 잘 비추는 카드",
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            )
        )
    )

    private val pastPresentFuture = SpreadDefinition(
        type = SpreadType.PastPresentFuture,
        title = "쓰리카드",
        description = "3장의 카드를 순서대로 배열해 상황을 간단히 읽습니다.",
        questionPlaceholder = "궁금한 상황을 짧게 적어주세요 (선택)",
        layout = SpreadLayout(columns = 3, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("ppf_past"),
                title = "첫번째 카드",
                description = "첫 흐름 또는 배경",
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("ppf_present"),
                title = "두번째 카드",
                description = "현재 핵심 메시지",
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("ppf_future"),
                title = "세번째 카드",
                description = "다음으로 이어질 가능성",
                order = 3,
                placement = SpreadPlacement(column = 2, row = 0)
            )
        )
    )

    private val energyAdvice = SpreadDefinition(
        type = SpreadType.EnergyAdvice,
        title = "투카드",
        description = "2장의 카드를 나란히 펼쳐 빠르게 핵심을 봅니다.",
        questionPlaceholder = "집중하려는 주제를 적어주세요 (선택)",
        layout = SpreadLayout(columns = 2, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("energy_now"),
                title = "첫번째 카드",
                description = "상황 또는 핵심 포인트",
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("energy_advice"),
                title = "두번째 카드",
                description = "보완 또는 조언",
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            )
        )
    )

    private val pathForward = SpreadDefinition(
        type = SpreadType.PathForward,
        title = "포카드",
        description = "4장의 카드로 상황과 다음 단계를 단계별로 훑습니다.",
        questionPlaceholder = "보고 싶은 상황을 적어주세요 (선택)",
        layout = SpreadLayout(columns = 4, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("path_now"),
                title = "첫번째 카드",
                description = "출발점 혹은 현재",
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_challenge"),
                title = "두번째 카드",
                description = "지나야 할 요소",
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_guidance"),
                title = "세번째 카드",
                description = "도움이 되는 관점",
                order = 3,
                placement = SpreadPlacement(column = 2, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_outcome"),
                title = "네번째 카드",
                description = "이어질 결과/다음 단계",
                order = 4,
                placement = SpreadPlacement(column = 3, row = 0)
            )
        )
    )

    private val celticCross = SpreadDefinition(
        type = SpreadType.CelticCross,
        title = "켈틱 크로스",
        description = "10장의 전통 스프레드로 상황을 다각도로 조망합니다.",
        questionPlaceholder = "깊이 들여다보고 싶은 질문을 적어보세요 (선택)",
        layout = SpreadLayout(columns = 4, rows = 4),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("celtic_present"),
                title = "현재 상황",
                description = "리딩의 중심 주제",
                order = 1,
                placement = SpreadPlacement(column = 1, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_crossing"),
                title = "교차 에너지",
                description = "도전 혹은 보완 요소",
                order = 2,
                placement = SpreadPlacement(column = 1, row = 1, rotationDegrees = 90f, zIndex = 1f)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_foundation"),
                title = "근본 원인",
                description = "숨은 뿌리, 무의식",
                order = 3,
                placement = SpreadPlacement(column = 1, row = 2)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_past"),
                title = "최근 과거",
                description = "지나간 영향",
                order = 4,
                placement = SpreadPlacement(column = 0, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_conscious"),
                title = "의식 / 가능성",
                description = "상황이 향하는 상단",
                order = 5,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_near_future"),
                title = "다가올 일",
                description = "머지않아 다가올 기류",
                order = 6,
                placement = SpreadPlacement(column = 2, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_self"),
                title = "나 자신",
                description = "질문자 상태",
                order = 7,
                placement = SpreadPlacement(column = 3, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_environment"),
                title = "환경 / 타인",
                description = "주변에서 오는 영향",
                order = 8,
                placement = SpreadPlacement(column = 3, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_hopes"),
                title = "희망과 두려움",
                description = "마음이 품은 양면",
                order = 9,
                placement = SpreadPlacement(column = 3, row = 2)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_outcome"),
                title = "잠재적 결과",
                description = "전체 흐름이 닿을 곳",
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
