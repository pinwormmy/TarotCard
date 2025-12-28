package com.pinwormmy.midoritarot.domain.spread

import androidx.compose.runtime.Immutable
import com.pinwormmy.midoritarot.core.localization.LocalizedString

@JvmInline
value class SpreadSlot(val id: String)

enum class SpreadType {
    OneCard,
    DailyCard,
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
    val title: LocalizedString,
    val description: LocalizedString,
    val order: Int,
    val placement: SpreadPlacement
)

@Immutable
data class SpreadDefinition(
    val type: SpreadType,
    val title: LocalizedString,
    val description: LocalizedString,
    val questionPlaceholder: LocalizedString,
    val layout: SpreadLayout,
    val positions: List<SpreadPosition>,
    val defaultUseReversed: Boolean = true
)

object SpreadCatalog {
    val dailyCardSlot = SpreadSlot("daily_card")

    private val oneCard = SpreadDefinition(
        type = SpreadType.OneCard,
        title = LocalizedString(
            ko = "원카드",
            en = "One Card",
            ja = "ワンカード",
            th = "ไพ่ใบเดียว",
        ),
        description = LocalizedString(
            ko = "오늘 필요한 핵심 메시지 한 장으로 빠르게 리딩합니다.",
            en = "A single card to receive the core message you need today.",
            ja = "今日必要な核心メッセージを1枚で素早く受け取ります。",
            th = "ไพ่ใบเดียวเพื่อรับสารหลักที่คุณต้องการในวันนี้แบบรวดเร็ว",
        ),
        questionPlaceholder = LocalizedString(
            ko = "(선택)질문을 입력하세요",
            en = "(Optional) Enter a question",
            ja = "（任意）質問を入力してください",
            th = "(ไม่บังคับ) พิมพ์คำถาม",
        ),
        layout = SpreadLayout(columns = 1, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("one_focus"),
                title = LocalizedString(
                    ko = "핵심 메시지",
                    en = "Core Message",
                    ja = "核心メッセージ",
                    th = "ข้อความหลัก",
                ),
                description = LocalizedString(
                    ko = "현재 상황을 가장 잘 비추는 카드",
                    en = "The card that best reflects your situation now.",
                    ja = "今の状況を最も映し出すカード。",
                    th = "ไพ่ที่สะท้อนสถานการณ์ปัจจุบันได้ชัดที่สุด",
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            )
        )
    )

    private val dailyCard = SpreadDefinition(
        type = SpreadType.DailyCard,
        title = LocalizedString(
            ko = "오늘 하루 운세 카드",
            en = "Today Fortune Card",
            ja = "今日の運勢カード",
            th = "ไพ่ทำนายประจำวัน",
        ),
        description = LocalizedString(
            ko = "오늘 하루의 메시지를 한 장으로 확인합니다.",
            en = "A single card to check today's message.",
            ja = "今日のメッセージを1枚で確認します。",
            th = "การ์ดหนึ่งใบสำหรับข้อความประจำวันนี้",
        ),
        questionPlaceholder = LocalizedString(
            ko = "",
            en = "",
            ja = "",
            th = "",
        ),
        layout = SpreadLayout(columns = 1, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = dailyCardSlot,
                title = LocalizedString(
                    ko = "오늘의 메시지",
                    en = "Today's Message",
                    ja = "今日のメッセージ",
                    th = "ข้อความวันนี้",
                ),
                description = LocalizedString(
                    ko = "오늘의 흐름을 알려주는 카드",
                    en = "The card that reflects today's flow.",
                    ja = "今日の流れを示すカード。",
                    th = "การ์ดที่สะท้อนถึงกระแสของวันนี้",
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0),
            )
        ),
        defaultUseReversed = false,
    )

    private val pastPresentFuture = SpreadDefinition(
        type = SpreadType.PastPresentFuture,
        title = LocalizedString(
            ko = "쓰리카드",
            en = "Three Card",
            ja = "スリーカード",
            th = "ไพ่สามใบ",
        ),
        description = LocalizedString(
            ko = "3장의 카드를 순서대로 배열해 상황을 간단히 읽습니다.",
            en = "Read the situation with three cards laid out in order.",
            ja = "3枚のカードを順に並べて状況をシンプルに読みます。",
            th = "วางไพ่สามใบตามลำดับเพื่ออ่านสถานการณ์อย่างง่าย",
        ),
        questionPlaceholder = LocalizedString(
            ko = "(선택)질문을 입력하세요",
            en = "(Optional) Enter a question",
            ja = "（任意）質問を入力してください",
            th = "(ไม่บังคับ) พิมพ์คำถาม",
        ),
        layout = SpreadLayout(columns = 3, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("ppf_past"),
                title = LocalizedString(
                    ko = "첫번째 카드",
                    en = "First Card",
                    ja = "1枚目のカード",
                    th = "ไพ่ใบที่หนึ่ง",
                ),
                description = LocalizedString(
                    ko = "첫 흐름 또는 배경",
                    en = "Initial flow or background.",
                    ja = "最初の流れ、あるいは背景。",
                    th = "กระแสแรกเริ่มหรือภูมิหลัง",
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("ppf_present"),
                title = LocalizedString(
                    ko = "두번째 카드",
                    en = "Second Card",
                    ja = "2枚目のカード",
                    th = "ไพ่ใบที่สอง",
                ),
                description = LocalizedString(
                    ko = "현재 핵심 메시지",
                    en = "The core message of the present.",
                    ja = "現在の核心メッセージ。",
                    th = "ข้อความหลักของปัจจุบัน",
                ),
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("ppf_future"),
                title = LocalizedString(
                    ko = "세번째 카드",
                    en = "Third Card",
                    ja = "3枚目のカード",
                    th = "ไพ่ใบที่สาม",
                ),
                description = LocalizedString(
                    ko = "다음으로 이어질 가능성",
                    en = "What is likely to unfold next.",
                    ja = "これから起こりそうな展開。",
                    th = "ความเป็นไปได้ที่กำลังจะตามมา",
                ),
                order = 3,
                placement = SpreadPlacement(column = 2, row = 0)
            )
        )
    )

    private val energyAdvice = SpreadDefinition(
        type = SpreadType.EnergyAdvice,
        title = LocalizedString(
            ko = "투카드",
            en = "Two Card",
            ja = "ツーカード",
            th = "ไพ่สองใบ",
        ),
        description = LocalizedString(
            ko = "2장의 카드를 나란히 펼쳐 빠르게 핵심을 봅니다.",
            en = "Two side‑by‑side cards to see the core quickly.",
            ja = "2枚を並べて素早く核心を見るスプレッド。",
            th = "วางไพ่สองใบเคียงกันเพื่อเห็นแก่นอย่างรวดเร็ว",
        ),
        questionPlaceholder = LocalizedString(
            ko = "(선택)질문을 입력하세요",
            en = "(Optional) Enter a question",
            ja = "（任意）質問を入力してください",
            th = "(ไม่บังคับ) พิมพ์คำถาม",
        ),
        layout = SpreadLayout(columns = 2, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("energy_now"),
                title = LocalizedString(
                    ko = "첫번째 카드",
                    en = "First Card",
                    ja = "1枚目のカード",
                    th = "ไพ่ใบที่หนึ่ง",
                ),
                description = LocalizedString(
                    ko = "상황 또는 핵심 포인트",
                    en = "Current situation or main point.",
                    ja = "現在の状況またはポイント。",
                    th = "สถานการณ์หรือประเด็นหลัก",
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("energy_advice"),
                title = LocalizedString(
                    ko = "두번째 카드",
                    en = "Second Card",
                    ja = "2枚目のカード",
                    th = "ไพ่ใบที่สอง",
                ),
                description = LocalizedString(
                    ko = "보완 또는 조언",
                    en = "Complementary energy or advice.",
                    ja = "補足するエネルギーやアドバイス。",
                    th = "พลังเสริมหรือคำแนะนำ",
                ),
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            )
        )
    )

    private val pathForward = SpreadDefinition(
        type = SpreadType.PathForward,
        title = LocalizedString(
            ko = "포카드",
            en = "Four Card",
            ja = "フォーカード",
            th = "ไพ่สี่ใบ",
        ),
        description = LocalizedString(
            ko = "4장의 카드로 상황과 다음 단계를 단계별로 훑습니다.",
            en = "Four cards to scan the situation and next steps.",
            ja = "4枚のカードで状況と次のステップを段階的に見ます。",
            th = "ใช้ไพ่สี่ใบเพื่อสำรวจสถานการณ์และขั้นตอนถัดไปทีละขั้น",
        ),
        questionPlaceholder = LocalizedString(
            ko = "(선택)질문을 입력하세요",
            en = "(Optional) Enter a question",
            ja = "（任意）質問を入力してください",
            th = "(ไม่บังคับ) พิมพ์คำถาม",
        ),
        layout = SpreadLayout(columns = 4, rows = 1),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("path_now"),
                title = LocalizedString(
                    ko = "첫번째 카드",
                    en = "First Card",
                    ja = "1枚目のカード",
                    th = "ไพ่ใบที่หนึ่ง",
                ),
                description = LocalizedString(
                    ko = "출발점 혹은 현재",
                    en = "Starting point or present.",
                    ja = "出発点または現在。",
                    th = "จุดเริ่มต้นหรือปัจจุบัน",
                ),
                order = 1,
                placement = SpreadPlacement(column = 0, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_challenge"),
                title = LocalizedString(
                    ko = "두번째 카드",
                    en = "Second Card",
                    ja = "2枚目のカード",
                    th = "ไพ่ใบที่สอง",
                ),
                description = LocalizedString(
                    ko = "지나야 할 요소",
                    en = "The obstacle to move through.",
                    ja = "乗り越えるべき要素。",
                    th = "สิ่งที่ต้องผ่านไป",
                ),
                order = 2,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_guidance"),
                title = LocalizedString(
                    ko = "세번째 카드",
                    en = "Third Card",
                    ja = "3枚目のカード",
                    th = "ไพ่ใบที่สาม",
                ),
                description = LocalizedString(
                    ko = "도움이 되는 관점",
                    en = "Perspective that helps.",
                    ja = "助けとなる視点。",
                    th = "มุมมองที่ช่วยได้",
                ),
                order = 3,
                placement = SpreadPlacement(column = 2, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("path_outcome"),
                title = LocalizedString(
                    ko = "네번째 카드",
                    en = "Fourth Card",
                    ja = "4枚目のカード",
                    th = "ไพ่ใบที่สี่",
                ),
                description = LocalizedString(
                    ko = "이어질 결과/다음 단계",
                    en = "Likely outcome or next step.",
                    ja = "予想される結果・次のステップ。",
                    th = "ผลลัพธ์ที่น่าจะเกิดขึ้นหรือขั้นตอนถัดไป",
                ),
                order = 4,
                placement = SpreadPlacement(column = 3, row = 0)
            )
        )
    )

    private val celticCross = SpreadDefinition(
        type = SpreadType.CelticCross,
        title = LocalizedString(
            ko = "켈틱 크로스",
            en = "Celtic Cross",
            ja = "ケルト十字",
            th = "เซลติกครอส",
        ),
        description = LocalizedString(
            ko = "10장의 전통 스프레드로 상황을 다각도로 조망합니다.",
            en = "A classic 10-card spread to view the situation from many angles.",
            ja = "10枚の伝統的なスプレッドで状況を多角的に見ます。",
            th = "สเปรดแบบคลาสสิก 10 ใบเพื่อมองสถานการณ์หลายมุม",
        ),
        questionPlaceholder = LocalizedString(
            ko = "(선택)질문을 입력하세요",
            en = "(Optional) Enter a question",
            ja = "（任意）質問を入力してください",
            th = "(ไม่บังคับ) พิมพ์คำถาม",
        ),
        layout = SpreadLayout(columns = 4, rows = 4),
        positions = listOf(
            SpreadPosition(
                slot = SpreadSlot("celtic_present"),
                title = LocalizedString(
                    ko = "현재 상황",
                    en = "Present Situation",
                    ja = "現在の状況",
                    th = "สถานการณ์ปัจจุบัน",
                ),
                description = LocalizedString(
                    ko = "리딩의 중심 주제",
                    en = "Core theme of the reading.",
                    ja = "リーディングの中心テーマ。",
                    th = "ธีมหลักของการอ่าน",
                ),
                order = 1,
                placement = SpreadPlacement(column = 1, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_crossing"),
                title = LocalizedString(
                    ko = "교차 에너지",
                    en = "Crossing Energy",
                    ja = "交差する要因",
                    th = "พลังที่ตัดกัน",
                ),
                description = LocalizedString(
                    ko = "도전 혹은 보완 요소",
                    en = "Challenge or assisting factor.",
                    ja = "課題または助けとなる要因。",
                    th = "ความท้าทายหรือปัจจัยที่ช่วยเสริม",
                ),
                order = 2,
                placement = SpreadPlacement(column = 1, row = 1, rotationDegrees = 90f, zIndex = 1f)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_foundation"),
                title = LocalizedString(
                    ko = "근본 원인",
                    en = "Foundation",
                    ja = "根本原因",
                    th = "รากฐาน",
                ),
                description = LocalizedString(
                    ko = "숨은 뿌리, 무의식",
                    en = "Hidden roots or subconscious.",
                    ja = "隠れた根・潜在意識。",
                    th = "รากที่ซ่อนอยู่หรือจิตใต้สำนึก",
                ),
                order = 3,
                placement = SpreadPlacement(column = 1, row = 2)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_past"),
                title = LocalizedString(
                    ko = "최근 과거",
                    en = "Recent Past",
                    ja = "最近の過去",
                    th = "อดีตที่ผ่านมาไม่นาน",
                ),
                description = LocalizedString(
                    ko = "지나간 영향",
                    en = "Influence that has passed.",
                    ja = "過ぎ去った影響。",
                    th = "อิทธิพลที่ผ่านไปแล้ว",
                ),
                order = 4,
                placement = SpreadPlacement(column = 0, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_conscious"),
                title = LocalizedString(
                    ko = "의식 / 가능성",
                    en = "Conscious / Potential",
                    ja = "意識・可能性",
                    th = "จิตสำนึก / ศักยภาพ",
                ),
                description = LocalizedString(
                    ko = "상황이 향하는 상단",
                    en = "Where things are consciously headed.",
                    ja = "意識的に向かっている方向。",
                    th = "ทิศทางที่จิตสำนึกกำลังมุ่งไป",
                ),
                order = 5,
                placement = SpreadPlacement(column = 1, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_near_future"),
                title = LocalizedString(
                    ko = "다가올 일",
                    en = "Near Future",
                    ja = "近い未来",
                    th = "อนาคตอันใกล้",
                ),
                description = LocalizedString(
                    ko = "머지않아 다가올 기류",
                    en = "Energy arriving soon.",
                    ja = "まもなく訪れる流れ。",
                    th = "กระแสที่จะมาถึงในไม่ช้า",
                ),
                order = 6,
                placement = SpreadPlacement(column = 2, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_self"),
                title = LocalizedString(
                    ko = "나 자신",
                    en = "Self",
                    ja = "自分自身",
                    th = "ตัวตน",
                ),
                description = LocalizedString(
                    ko = "질문자 상태",
                    en = "State of the querent.",
                    ja = "質問者の状態。",
                    th = "สภาพของผู้ถาม",
                ),
                order = 7,
                placement = SpreadPlacement(column = 3, row = 0)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_environment"),
                title = LocalizedString(
                    ko = "환경 / 타인",
                    en = "Environment / Others",
                    ja = "環境・周囲",
                    th = "สภาพแวดล้อม / คนรอบข้าง",
                ),
                description = LocalizedString(
                    ko = "주변에서 오는 영향",
                    en = "Influences from surroundings.",
                    ja = "周囲からの影響。",
                    th = "อิทธิพลจากสิ่งรอบตัว",
                ),
                order = 8,
                placement = SpreadPlacement(column = 3, row = 1)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_hopes"),
                title = LocalizedString(
                    ko = "희망과 두려움",
                    en = "Hopes and Fears",
                    ja = "希望と恐れ",
                    th = "ความหวังและความกลัว",
                ),
                description = LocalizedString(
                    ko = "마음이 품은 양면",
                    en = "What the heart hopes for and fears.",
                    ja = "心が抱く希望と恐れ。",
                    th = "สองด้านที่ใจเก็บไว้",
                ),
                order = 9,
                placement = SpreadPlacement(column = 3, row = 2)
            ),
            SpreadPosition(
                slot = SpreadSlot("celtic_outcome"),
                title = LocalizedString(
                    ko = "잠재적 결과",
                    en = "Potential Outcome",
                    ja = "可能な結果",
                    th = "ผลลัพธ์ที่เป็นไปได้",
                ),
                description = LocalizedString(
                    ko = "전체 흐름이 닿을 곳",
                    en = "Where the overall flow may land.",
                    ja = "全体の流れが辿り着きそうな場所。",
                    th = "จุดที่กระแสโดยรวมอาจไปถึง",
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
        when (type) {
            SpreadType.DailyCard -> dailyCard
            else -> allDefinitions.firstOrNull { it.type == type } ?: default
        }
}
