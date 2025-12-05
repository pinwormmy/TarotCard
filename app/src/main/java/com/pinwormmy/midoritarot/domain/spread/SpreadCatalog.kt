package com.pinwormmy.midoritarot.domain.spread

import androidx.compose.runtime.Immutable

@JvmInline
value class SpreadSlot(val id: String)

enum class SpreadType {
    OneCard,
    PastPresentFuture,
    EnergyAdvice,
    PathForward,
    RelationshipH,
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
        title = "과거 · 현재 · 미래",
        description = "시간의 흐름에 따라 사건을 조망하는 3장 스프레드입니다.",
        questionPlaceholder = "궁금한 상황을 묘사해 주세요 (선택)",
        layout = SpreadLayout(columns = 3, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("ppf_past"),
                title = "과거",
                description = "지금의 상황을 만든 배경",
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("ppf_present"),
                title = "현재",
                description = "현재 드러난 모습",
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("ppf_future"),
                title = "미래",
                description = "이 흐름이 닿을 가능성",
                order = 3,
                placement = SpreadPlacement(column = 2, row = 0)
            )
        )
    )

    private val energyAdvice = SpreadDefinition(
        type = SpreadType.EnergyAdvice,
        title = "에너지와 조언",
        description = "현재 흐름을 읽고 그에 맞는 한 장의 조언을 받습니다.",
        questionPlaceholder = "집중하려는 주제를 입력하세요 (선택)",
        layout = SpreadLayout(columns = 2, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("energy_now"),
                title = "현재 에너지",
                description = "몸과 마음에 깃든 기류",
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("energy_advice"),
                title = "조언",
                description = "흐름을 바르게 쓰는 방법",
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            )
        )
    )

    private val pathForward = SpreadDefinition(
        type = SpreadType.PathForward,
        title = "앞으로 나아갈 길",
        description = "4장의 카드로 여정의 현 위치와 다음 단계를 설계합니다.",
        questionPlaceholder = "달성하고 싶은 목표를 적어보세요 (선택)",
        layout = SpreadLayout(columns = 4, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("path_now"),
                title = "현재 위치",
                description = "지금 진행 상황",
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_challenge"),
                title = "장애물",
                description = "앞을 가로막는 요소",
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_guidance"),
                title = "가이드",
                description = "다음 선택을 비추는 힌트",
                order = 3,
                placement = SpreadPlacement(column = 2, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_outcome"),
                title = "결과",
                description = "방향을 따랐을 때의 모습",
                order = 4,
                placement = SpreadPlacement(column = 3, row = 0)
            )
        )
    )

    private val relationshipH = SpreadDefinition(
        type = SpreadType.RelationshipH,
        title = "관계",
        description = "H 모양 배치로 두 사람 사이의 흐름을 세밀하게 읽습니다.",
        questionPlaceholder = "어떤 관계를 탐색하고 싶나요? (선택)",
        layout = SpreadLayout(columns = 3, rows = 3),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("rel_you_top"),
                title = "나의 시선",
                description = "지금 내가 보는 장면",
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("rel_you_core"),
                title = "나의 감정",
                description = "감정의 중심",
                order = 2,
                placement = SpreadPlacement(column = 0, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("rel_you_bottom"),
                title = "숨겨진 영향",
                description = "겉으로 드러나지 않은 에너지",
                order = 3,
                placement = SpreadPlacement(column = 0, row = 2)
            ),
            SpreadPosition(
                slot = SpreadSlot("rel_bridge"),
                title = "관계의 연결",
                description = "두 사람을 잇는 고리",
                order = 4,
                placement = SpreadPlacement(column = 1, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("rel_other_top"),
                title = "상대의 시선",
                description = "상대가 바라보는 화면",
                order = 5,
                placement = SpreadPlacement(column = 2, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("rel_other_core"),
                title = "상대의 감정",
                description = "마음속의 진심",
                order = 6,
                placement = SpreadPlacement(column = 2, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("rel_future"),
                title = "함께 갈 미래",
                description = "관계가 만들어 갈 방향",
                order = 7,
                placement = SpreadPlacement(column = 2, row = 2)
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
        oneCard,
        pastPresentFuture,
        energyAdvice,
        pathForward,
        relationshipH,
        celticCross
    )

    val default: SpreadDefinition = pastPresentFuture

    val all: List<SpreadDefinition> = allDefinitions

    fun find(type: SpreadType): SpreadDefinition =
        allDefinitions.firstOrNull { it.type == type } ?: default
}
