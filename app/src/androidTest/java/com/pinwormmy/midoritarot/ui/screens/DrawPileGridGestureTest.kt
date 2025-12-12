package com.pinwormmy.midoritarot.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.components.CARD_LANDSCAPE_RATIO
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawPileGridGestureTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dragGesture_isNotCancelledWhenDrawnCountUpdatesMidGesture() {
        composeRule.mainClock.autoAdvance = false

        val selectedIds = mutableListOf<String>()

        val maxWidth = 400.dp
        val maxHeight = 800.dp
        val columnSpacing = 32.dp
        val cardWidth = (maxWidth - columnSpacing) / 2f
        val cardHeight = cardWidth / CARD_LANDSCAPE_RATIO

        val cardWidthPx = with(composeRule.density) { cardWidth.toPx() }
        val cardHeightPx = with(composeRule.density) { cardHeight.toPx() }
        val spacingPx = with(composeRule.density) { columnSpacing.toPx() }

        val card1Center = Offset(cardWidthPx / 2f, cardHeightPx / 2f)
        val card2Center = Offset(cardWidthPx + spacingPx + cardWidthPx / 2f, cardHeightPx / 2f)

        composeRule.setContent {
            val hapticFeedback = remember {
                object : HapticFeedback {
                    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) = Unit
                }
            }

            // [수정 1] var ... by 대신 val ... = remember { ... } 사용
            val drawnCountState = remember { mutableIntStateOf(0) }
            val disabledIdsState = remember { mutableStateOf(emptySet<String>()) }

            val cards = remember {
                listOf(
                    TarotCardModel(
                        id = "c1",
                        name = "c1",
                        arcana = "",
                        uprightMeaning = "",
                        reversedMeaning = "",
                        description = "",
                        keywords = emptyList(),
                    ),
                    TarotCardModel(
                        id = "c2",
                        name = "c2",
                        arcana = "",
                        uprightMeaning = "",
                        reversedMeaning = "",
                        description = "",
                        keywords = emptyList(),
                    )
                )
            }

            DrawPileGrid(
                modifier = Modifier
                    .size(maxWidth, maxHeight)
                    .testTag("grid"),
                cards = cards,
                disabledCardIds = disabledIdsState.value, // .value로 읽기
                drawnCount = drawnCountState.intValue,    // .intValue로 읽기
                totalSlots = 3,
                hapticsEnabled = false,
                hapticFeedback = hapticFeedback,
                selectionLocked = false,
                onDealAnimationFinished = {},
                onCardSelected = { card -> // 불필요한 Suppress 제거 가능
                    selectedIds.add(card.id)
                    // [수정 2] 값 할당 방식 변경 (Setter 호출이므로 경고 없음)
                    drawnCountState.intValue = selectedIds.size
                    disabledIdsState.value += card.id
                }
            )
        }

        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()

        val grid = composeRule.onNodeWithTag("grid")

        grid.performTouchInput {
            down(card1Center)
            up()
        }

        grid.performTouchInput { down(card2Center) }

        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()

        grid.performTouchInput { up() }

        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(listOf("c1", "c2"), selectedIds)
        }
    }

    @Test
    fun gesturesAreDisabledAfterSelectingAllSlots() {
        composeRule.mainClock.autoAdvance = false

        val selectedIds = mutableListOf<String>()

        val maxWidth = 400.dp
        val columnSpacing = 32.dp
        val cardWidth = (maxWidth - columnSpacing) / 2f
        val cardHeight = cardWidth / CARD_LANDSCAPE_RATIO

        val cardWidthPx = with(composeRule.density) { cardWidth.toPx() }
        val cardHeightPx = with(composeRule.density) { cardHeight.toPx() }
        val spacingPx = with(composeRule.density) { columnSpacing.toPx() }

        val card1Center = Offset(cardWidthPx / 2f, cardHeightPx / 2f)
        val card2Center = Offset(cardWidthPx + spacingPx + cardWidthPx / 2f, cardHeightPx / 2f)

        composeRule.setContent {
            val hapticFeedback = remember {
                object : HapticFeedback {
                    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) = Unit
                }
            }

            // [수정 1] 동일하게 val State 객체로 변경
            val drawnCountState = remember { mutableIntStateOf(0) }
            val disabledIdsState = remember { mutableStateOf(emptySet<String>()) }

            val cards = remember {
                listOf(
                    TarotCardModel(
                        id = "c1",
                        name = "c1",
                        arcana = "",
                        uprightMeaning = "",
                        reversedMeaning = "",
                        description = "",
                        keywords = emptyList(),
                    ),
                    TarotCardModel(
                        id = "c2",
                        name = "c2",
                        arcana = "",
                        uprightMeaning = "",
                        reversedMeaning = "",
                        description = "",
                        keywords = emptyList(),
                    )
                )
            }

            DrawPileGrid(
                modifier = Modifier
                    .size(maxWidth, 800.dp)
                    .testTag("grid"),
                cards = cards,
                disabledCardIds = disabledIdsState.value,
                drawnCount = drawnCountState.intValue,
                totalSlots = 1,
                hapticsEnabled = false,
                hapticFeedback = hapticFeedback,
                selectionLocked = false,
                onDealAnimationFinished = {},
                onCardSelected = { card ->
                    selectedIds.add(card.id)
                    // [수정 2] 값 업데이트
                    drawnCountState.intValue = selectedIds.size
                    disabledIdsState.value = disabledIdsState.value + card.id
                }
            )
        }

        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()

        val grid = composeRule.onNodeWithTag("grid")

        grid.performTouchInput {
            down(card1Center)
            up()
        }
        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()

        grid.performTouchInput {
            down(card2Center)
            up()
        }
        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(1, selectedIds.size)
        }
    }
}