@file:Suppress(
    "UNUSED_VALUE",
    "UnusedAssignment",
    "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE",
    "ComposeApplierCallMismatch",
    "ComposeApplierParameterMismatch",
    "ComposeApplierDeclarationMismatch", "COMPOSE_APPLIER_CALL_MISMATCH"
)

package com.pinwormmy.midoritarot.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.UiComposable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.compose.ui.res.stringResource
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.components.CardBackArt
import com.pinwormmy.midoritarot.ui.components.CardDeck
import com.pinwormmy.midoritarot.ui.components.CARD_ASPECT_RATIO
import com.pinwormmy.midoritarot.ui.components.CARD_LANDSCAPE_RATIO
import com.pinwormmy.midoritarot.ui.components.ShufflePhase
import com.pinwormmy.midoritarot.ui.components.TarotCardShape
import com.pinwormmy.midoritarot.ui.components.rememberCardBackPainter
import com.pinwormmy.midoritarot.ui.state.SpreadFlowUiState
import com.pinwormmy.midoritarot.ui.theme.LocalHapticsEnabled
import com.pinwormmy.midoritarot.ui.theme.HapticsPlayer
import androidx.compose.ui.platform.LocalWindowInfo
import com.pinwormmy.midoritarot.ui.theme.LocalUiHeightScale
import com.pinwormmy.midoritarot.ui.theme.TarotUiDefaults
import com.pinwormmy.midoritarot.ui.components.computeCardSizeLimit
import com.pinwormmy.midoritarot.ui.components.windowHeightDp
import com.pinwormmy.midoritarot.R
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.math.ceil

private const val LANDSCAPE_CARD_RATIO = CARD_LANDSCAPE_RATIO
private val GRID_COLUMN_SPACING = 32.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShuffleAndDrawScreen(
    uiState: SpreadFlowUiState,
    modifier: Modifier = Modifier,
    onDeckTap: () -> Unit,
    onCutRequest: () -> Unit,
    onCutSelect: (Int) -> Unit,
    onShowGrid: () -> Unit,
    onCardSelected: (TarotCardModel) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val uiScale = LocalUiHeightScale.current
    val cardSizeLimit = computeCardSizeLimit(
        screenHeightDp = windowHeightDp(windowInfo, density).toInt(),
        scaleFactor = uiScale,
        heightFraction = 0.7f
    )

    var shufflePhase by remember { mutableStateOf(ShufflePhase.Idle) }
    val drawnIds = remember(uiState.drawnCards) {
        uiState.drawnCards.values.map { it.card.id }.toSet()
    }
    val hapticFeedback = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val context = LocalContext.current
    val cardBackPainter = rememberCardBackPainter()

    val deckTapWithHaptics = {
        if (hapticsEnabled) {
            HapticsPlayer.cardTap(context, hapticFeedback)
        }
        onDeckTap()
    }
    val gridRevealWithHaptics = {
        if (hapticsEnabled) {
            HapticsPlayer.shuffle(context, hapticFeedback)
        }
        onShowGrid()
    }
    val cutRequestWithHaptics = {
        if (hapticsEnabled) {
            HapticsPlayer.cardTap(context, hapticFeedback)
        }
        onCutRequest()
    }
    val cutSelectWithHaptics: (Int) -> Unit = { index ->
        if (hapticsEnabled) {
            HapticsPlayer.shuffle(context, hapticFeedback)
        }
        onCutSelect(index)
    }

    val deckInteractionEnabled by remember(uiState.gridVisible, uiState.cutMode, shufflePhase) {
        derivedStateOf {
            !uiState.gridVisible && !uiState.cutMode &&
                (shufflePhase == ShufflePhase.Idle || shufflePhase == ShufflePhase.Finished)
        }
    }

    var dealAnimationFinished by remember(uiState.shuffleTrigger, uiState.drawPile) {
        mutableStateOf(false)
    }

    val animationLocked by remember(shufflePhase, uiState.gridVisible, dealAnimationFinished) {
        derivedStateOf {
            (shufflePhase != ShufflePhase.Idle && shufflePhase != ShufflePhase.Finished) ||
                (uiState.gridVisible && !dealAnimationFinished)
        }
    }

    val selectionLocked by remember(uiState.pendingSlots.size, uiState.drawnCards.size) {
        derivedStateOf { uiState.drawnCards.size >= uiState.pendingSlots.size }
    }

    LaunchedEffect(uiState.gridVisible) {
        if (!uiState.gridVisible) {
            dealAnimationFinished = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 타이틀 / 질문
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = uiState.spread.title.resolve(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.questionText.isNotBlank()) {
                    Text(
                        text = stringResource(
                            id = R.string.reading_question_prefix,
                            uiState.questionText
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 상단 카드 영역
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // 셔플/컷용 세로 카드 비율
                    val widthFactor = if (uiState.cutMode) 0.9f else 0.7f
                    val maxWidthByHeight = maxHeight * CARD_ASPECT_RATIO
                    val proposedWidth = (maxWidth * widthFactor).coerceAtMost(maxWidthByHeight)
                    val cappedWidth = proposedWidth.coerceAtMost(cardSizeLimit.maxWidth)
                    val cardHeight = (cappedWidth / CARD_ASPECT_RATIO).coerceAtMost(cardSizeLimit.maxHeight)
                    val cardWidth = cardHeight * CARD_ASPECT_RATIO

                    if (uiState.gridVisible) {
                        // ✅ 드로우 그리드: 여기 안에서만 가로 카드 처리
                            DrawPileGrid(
                                modifier = Modifier.fillMaxSize(),
                                cards = uiState.drawPile,
                                disabledCardIds = drawnIds,
                                drawnCount = uiState.drawnCards.size,
                                totalSlots = uiState.pendingSlots.size,
                                hapticsEnabled = hapticsEnabled,
                                hapticFeedback = hapticFeedback,
                                selectionLocked = animationLocked || selectionLocked,
                                onDealAnimationFinished = { dealAnimationFinished = true },
                                onCardSelected = onCardSelected
                            )
                    } else {
                        // cutMode 전환 애니메이션: 한 덱 ↔ 3덱
                        val transition = updateTransition(
                            targetState = uiState.cutMode,
                            label = "cutTransition"
                        )

                        val deckScale by transition.animateFloat(
                            transitionSpec = { tween(durationMillis = 350) },
                            label = "deckScale"
                        ) { cut ->
                            if (cut) 0.85f else 1f
                        }

                        val deckAlpha by transition.animateFloat(
                            transitionSpec = { tween(durationMillis = 300) },
                            label = "deckAlpha"
                        ) { cut ->
                            if (cut) 0f else 1f
                        }

                        val cutAlpha by transition.animateFloat(
                            transitionSpec = { tween(durationMillis = 300) },
                            label = "cutAlpha"
                        ) { cut ->
                            if (cut) 1f else 0f
                        }

                        val fanOutProgress by transition.animateFloat(
                            transitionSpec = { tween(durationMillis = 350) },
                            label = "fanOut"
                        ) { cut ->
                            if (cut) 1f else 0f
                        }

                        Box(
                            modifier = Modifier
                                .width(cardWidth)
                                .height(cardHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            // 1) 항상 그려지는 "한 덱" (세로 카드)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        scaleX = deckScale
                                        scaleY = deckScale
                                        alpha = deckAlpha
                                    }
                            ) {
                                CardBackImage(
                                    modifier = Modifier.fillMaxSize(),
                                    enabled = deckInteractionEnabled && !animationLocked,
                                    onClick = deckTapWithHaptics,
                                    painter = cardBackPainter
                                )
                                CardDeck(
                                    modifier = Modifier.fillMaxSize(),
                                    width = cardWidth,
                                    height = cardHeight,
                                    shuffleTrigger = uiState.shuffleTrigger,
                                    painter = cardBackPainter,
                                    onPhaseChanged = {
                                        shufflePhase = it
                                        if (hapticsEnabled) {
                                            when (it) {
                                                ShufflePhase.Split,
                                                ShufflePhase.Merge -> HapticsPlayer.shuffle(context, hapticFeedback)
                                                else -> Unit
                                            }
                                        }
                                    },
                                    onRiffleBeat = {
                                        if (hapticsEnabled) {
                                            HapticsPlayer.shuffle(context, hapticFeedback)
                                        }
                                    }
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(24.dp))
                                        .clickable(
                                            enabled = deckInteractionEnabled && !animationLocked,
                                            onClick = deckTapWithHaptics
                                        )
                                )
                            }

                            // 2) 컷 모드 3덱 (세로 카드 느낌 유지)
                            if (uiState.cutMode || cutAlpha > 0.01f) {
                                CutModeScene(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = cutAlpha },
                                    fanProgress = fanOutProgress,
                                    onStackChosen = cutSelectWithHaptics
                                )
                            }
                        }
                    }
                }
            }

            // 하단 버튼 Row (그리드 아닐 때만)
            if (!uiState.gridVisible && !uiState.cutMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlineLargeButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.action_cut),
                        enabled = !animationLocked,
                        onClick = cutRequestWithHaptics
                    )
                    OutlineLargeButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.action_draw),
                        enabled = !animationLocked,
                        onClick = gridRevealWithHaptics
                    )
                }
            }
        }
    }

    // 하단 안내 메시지
    val instruction = uiState.nextInstruction
    if (instruction != null && (!uiState.gridVisible || dealAnimationFinished)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                tonalElevation = 6.dp
            ) {
                Text(
                    text = instruction,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CardBackImage(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    painter: Painter? = null
) {
    Box(
        modifier = modifier
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        repeat(3) { offset ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = (offset * 8).dp)
                    .clip(TarotCardShape)
            ) {
                CardBackArt(
                    modifier = Modifier.fillMaxSize(),
                    overlay = Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0x55000000))
                    ),
                    shape = TarotCardShape,
                    painterOverride = painter
                )
            }
        }
    }
}

@Composable
private fun OutlineLargeButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier.height(56.dp),
        border = BorderStroke(1.dp, TarotUiDefaults.outline(0.5f)),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        enabled = enabled,
        onClick = onClick
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun DrawPileGrid(
    modifier: Modifier = Modifier,
    cards: List<TarotCardModel>,
    disabledCardIds: Set<String>,
    drawnCount: Int,
    totalSlots: Int,
    hapticsEnabled: Boolean,
    hapticFeedback: HapticFeedback,
    selectionLocked: Boolean = false,
    onDealAnimationFinished: () -> Unit = {},
    onCardSelected: (TarotCardModel) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        val density = LocalDensity.current
        val selectionEvents = remember { MutableSharedFlow<String>(extraBufferCapacity = 16) }
        var hoveredCardId by remember { mutableStateOf<String?>(null) }
        val cardBackPainter = rememberCardBackPainter()
        val localSelectedIdsState = remember(cards) { mutableStateOf(setOf<String>()) }
        val disabledCardIdsState = rememberUpdatedState(disabledCardIds)
        val drawnCountState = rememberUpdatedState(drawnCount)
        val selectionLockedState = rememberUpdatedState(selectionLocked)

        val columnSpacing = GRID_COLUMN_SPACING
        val totalRows = (cards.size + 1) / 2

        // 가로 카드 비율: 세로 카드(0.66:1)를 눕힌 1.5:1 비율
        val landscapeRatio = LANDSCAPE_CARD_RATIO
        val cardWidth = (maxWidth - columnSpacing) / 2f
        val cardHeight = cardWidth / landscapeRatio

        val cardWidthPx = with(density) { cardWidth.toPx() }
        val cardHeightPx = with(density) { cardHeight.toPx() }

        val columnCount = ceil(cards.size / 2f).toInt()
        val usableHeight = maxHeight - cardHeight
        val stepY = if (columnCount > 1) usableHeight / (columnCount - 1) else 0.dp
        // 드로우 시 카드 뿌리기 속도 2배 가속: 간격·재생 시간 절반
        val dealStaggerMillis = 10L
        val dealAnimationMillis = 110

        val totalWidth = cardWidth * 2 + columnSpacing
        val leftColumnX = (maxWidth - totalWidth) / 2f
        val rightColumnX = leftColumnX + cardWidth + columnSpacing

        val startXPx = -cardWidthPx
        val startYPx = -cardHeightPx
        val maxHeightPx = with(density) { maxHeight.toPx() }

        data class CardPlacement(
            val card: TarotCardModel,
            val targetXPx: Float,
            val targetYPx: Float,
            val dealOrderIndex: Int
        )

        val placements = cards.mapIndexed { index, card ->
            val columnIndex = index % 2
            val rowIndex = index / 2
            val dealOrderIndex = if (columnIndex == 1) rowIndex else totalRows + rowIndex
            val targetXDp = if (columnIndex == 0) leftColumnX else rightColumnX
            val targetYDp = stepY * rowIndex
            CardPlacement(
                card = card,
                targetXPx = with(density) { targetXDp.toPx() },
                targetYPx = with(density) { targetYDp.toPx() },
                dealOrderIndex = dealOrderIndex
            )
        }

        fun findCardIdAt(position: Offset): String? {
            val combinedDisabled = disabledCardIdsState.value + localSelectedIdsState.value
            return placements.asReversed().firstOrNull { placement ->
                val withinX = position.x in placement.targetXPx..(placement.targetXPx + cardWidthPx)
                val withinY = position.y in placement.targetYPx..(placement.targetYPx + cardHeightPx)
                withinX && withinY && !combinedDisabled.contains(placement.card.id)
            }?.card?.id
        }

        val maxDealIndex = placements.maxOfOrNull { it.dealOrderIndex } ?: 0
        val gesturesEnabledState = remember(cards) { mutableStateOf(false) }

        LaunchedEffect(cards) {
            gesturesEnabledState.value = false
            if (cards.isEmpty()) return@LaunchedEffect

            val totalDelay = dealStaggerMillis * maxDealIndex + dealAnimationMillis.toLong()
            if (totalDelay > 0) {
                delay(totalDelay)
            }
            gesturesEnabledState.value = true
            onDealAnimationFinished()
        }

        val pointerModifier = Modifier
            .fillMaxSize()
            .pointerInput(
                cards,
                maxWidth,
                maxHeight,
                gesturesEnabledState.value,
                totalSlots
            ) {
                if (!gesturesEnabledState.value) {
                    awaitCancellation()
                }

                fun selectionLimitReached(): Boolean =
                    selectionLockedState.value ||
                        (drawnCountState.value + localSelectedIdsState.value.size >= totalSlots)

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    try {
                        if (selectionLimitReached()) {
                            return@awaitEachGesture
                        }

                        hoveredCardId = findCardIdAt(down.position)
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: continue
                            if (selectionLimitReached()) {
                                hoveredCardId = null
                                break
                            }
                            hoveredCardId = findCardIdAt(change.position)
                            if (!change.pressed) {
                                if (selectionLimitReached()) {
                                    hoveredCardId = null
                                    break
                                }
                                val selectedId = hoveredCardId
                                hoveredCardId = null
                                if (selectedId != null) {
                                    localSelectedIdsState.value =
                                        localSelectedIdsState.value + selectedId

                                    if (hapticsEnabled) {
                                        hapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.LongPress
                                        )
                                    }
                                    selectionEvents.tryEmit(selectedId)
                                }
                                break
                            }
                            if (change.isConsumed) {
                                hoveredCardId = null
                                break
                            }
                        }
                    } finally {
                        hoveredCardId = null
                    }
                }
            }

        Box(modifier = pointerModifier) {
            placements.forEach { placement ->
                val card = placement.card
                val appear = remember(card.id) { Animatable(0f) }
                val exitProgress = remember(card.id) { Animatable(0f) }
                val isExiting = remember(card.id) { mutableStateOf(false) }

                LaunchedEffect(card.id) {
                    delay(dealStaggerMillis * placement.dealOrderIndex)
                    appear.animateTo(1f, tween(dealAnimationMillis))
                }

                LaunchedEffect(isExiting.value) {
                    if (isExiting.value) {
                        exitProgress.animateTo(1f, tween(400))
                        delay(50)
                        onCardSelected(card)
                        localSelectedIdsState.value =
                            localSelectedIdsState.value - card.id
                    }
                }

                LaunchedEffect(card.id) {
                    selectionEvents.collect { targetId ->
                        if (targetId == card.id && !isExiting.value) {
                            isExiting.value = true
                        }
                    }
                }

                val isDisabled = disabledCardIds.contains(card.id)
                val animatedTranslationX = lerp(startXPx, placement.targetXPx, appear.value)
                val animatedTranslationY = lerp(startYPx, placement.targetYPx, appear.value)
                val exitShiftY = exitProgress.value * (maxHeightPx + cardHeightPx)
                val baseScale = 0.9f + 0.1f * appear.value
                val hoverScale = if (!isDisabled && hoveredCardId == card.id) 1.05f else 1f
                val targetAlpha = if (isDisabled) 0f else 1f
                val layerAlpha =
                    (appear.value * (1f - exitProgress.value)).coerceIn(0f, targetAlpha)

                // 🔥 여기 Box가 "가로 카드" 컨테이너 + 움직임 담당
                Box(
                    modifier = Modifier
                        .zIndex(if (isExiting.value || exitProgress.value > 0f) 1f else 0f)
                        .width(cardWidth)
                        .height(cardHeight)
                        .graphicsLayer {
                            translationX = animatedTranslationX
                            translationY = animatedTranslationY + exitShiftY
                            this.alpha = layerAlpha
                            val scale = baseScale * hoverScale
                            scaleX = scale
                            scaleY = scale
                        },
                    contentAlignment = Alignment.Center
                ) {
                    RotatedCardBack(
                        modifier = Modifier.fillMaxSize(),
                        painter = cardBackPainter
                    )
                }
            }
        }
    }
}

@UiComposable
@Composable
private fun RotatedCardBack(
    modifier: Modifier = Modifier,
    painter: Painter?
) {
    // 드로우 그리드용 가로 카드: 자식은 세로 비율로 측정, 부모는 가로 비율을 노출한 뒤
    // 배치 단계에서 90도 회전과 모서리 클립을 적용하여 카드와 이미지 방향을 일치시킨다.
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = 90f
                        transformOrigin = TransformOrigin.Center
                        shape = TarotCardShape
                        clip = true
                    }
            ) {
                CardBackArt(
                    modifier = Modifier.fillMaxSize(),
                    overlay = Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0x66000000))
                    ),
                    shape = TarotCardShape,
                    painterOverride = painter
                )
            }
        }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(
            constraints.copy(
                minWidth = constraints.minHeight,
                maxWidth = constraints.maxHeight,
                minHeight = constraints.minWidth,
                maxHeight = constraints.maxWidth
            )
        )

        // 부모(가로 셀) 기준 크기를 노출
        layout(placeable.height, placeable.width) {
            val offsetX = (placeable.height - placeable.width) / 2
            val offsetY = (placeable.width - placeable.height) / 2
            placeable.placeRelative(offsetX, offsetY)
        }
    }
}

/**
 * 컷 모드에서 보여줄 3덱 장면.
 */
@Composable
private fun CutModeScene(
    modifier: Modifier = Modifier,
    fanProgress: Float,
    onStackChosen: (Int) -> Unit
) {
    val density = LocalDensity.current
    val cardBackPainter = rememberCardBackPainter()
    var animationCounter by remember { mutableStateOf(0) }

    val firstMergeProgress = remember { Animatable(0f) }
    val secondMergeProgress = remember { Animatable(0f) }
    val zoomProgress = remember { Animatable(0f) }

    var cutStage by remember { mutableStateOf<CutStage>(CutStage.Idle) }

    LaunchedEffect(fanProgress) {
        if (fanProgress <= 0.01f) {
            cutStage = CutStage.Idle
            firstMergeProgress.snapTo(0f)
            secondMergeProgress.snapTo(0f)
            zoomProgress.snapTo(0f)
        }
    }

    LaunchedEffect((cutStage as? CutStage.FirstMerge)?.animationKey) {
        val stage = cutStage as? CutStage.FirstMerge ?: return@LaunchedEffect
        firstMergeProgress.snapTo(0f)
        firstMergeProgress.animateTo(1f, tween(380))
        cutStage = CutStage.SecondReady(
            combined = stage.target,
            remaining = stage.remaining,
            lifted = null
        )
    }

    LaunchedEffect((cutStage as? CutStage.SecondMerge)?.animationKey) {
        val stage = cutStage as? CutStage.SecondMerge ?: return@LaunchedEffect
        secondMergeProgress.snapTo(0f)
        secondMergeProgress.animateTo(1f, tween(360))
        cutStage = CutStage.FinalZoom(stage.target, animationCounter++)
    }

    LaunchedEffect((cutStage as? CutStage.FinalZoom)?.animationKey) {
        val stage = cutStage as? CutStage.FinalZoom ?: return@LaunchedEffect
        zoomProgress.snapTo(0f)
        zoomProgress.animateTo(1f, tween(320))
        onStackChosen(stage.finalIndex)
        cutStage = CutStage.Completed
    }

    fun handleTap(index: Int) {
        if (fanProgress < 0.95f) return
        when (val stage = cutStage) {
            CutStage.Idle -> cutStage = CutStage.SourceSelected(index)
            is CutStage.SourceSelected -> {
                if (index == stage.source) {
                    cutStage = CutStage.Idle
                } else {
                    val remaining = remainingIndex(stage.source, index)
                    cutStage = CutStage.FirstMerge(
                        source = stage.source,
                        target = index,
                        remaining = remaining,
                        animationKey = animationCounter++
                    )
                }
            }
            is CutStage.SecondReady -> {
                if (index != stage.combined && index != stage.remaining) return
                when {
                    stage.lifted == null -> cutStage = stage.copy(lifted = index)
                    stage.lifted == index -> cutStage = stage.copy(lifted = null)
                    else -> {
                    val source = stage.lifted
                        cutStage = CutStage.SecondMerge(
                            combined = stage.combined,
                            remaining = stage.remaining,
                            source = source,
                            target = index,
                            animationKey = animationCounter++
                        )
                    }
                }
            }
            else -> Unit
        }
    }

    val firstMergeValue = firstMergeProgress.value
    val secondMergeValue = secondMergeProgress.value
    val zoomValue = zoomProgress.value

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val fullWidth = maxWidth
        val pileWidth = fullWidth * 0.28f
        val pileHeight = pileWidth / CARD_ASPECT_RATIO
        val baseSpacing = 0.35f
        val liftY = 0.08f

        fun baseX(index: Int): Float = when (index) {
            0 -> -1f
            1 -> 0f
            else -> 1f
        } * baseSpacing * fanProgress

        data class Visual(
            val offsetXFraction: Float,
            val offsetYFraction: Float,
            val scale: Float,
            val alpha: Float,
            val clickable: Boolean
        )

        fun visualFor(index: Int): Visual {
            var x = baseX(index)
            var y = 0f
            var scale = 1f
            var alpha = if (fanProgress > 0.05f) 1f else 0f
            var clickable = true

            when (val stage = cutStage) {
                CutStage.Idle -> Unit
                is CutStage.SourceSelected -> {
                    if (index == stage.source) {
                        y = -liftY
                        scale = 1.08f
                    }
                }
                is CutStage.FirstMerge -> {
                    clickable = false
                    when (index) {
                        stage.source -> {
                            val start = baseX(stage.source)
                            val end = baseX(stage.target)
                            x = lerp(start, end, firstMergeValue)
                            y = lerp(-liftY, 0f, firstMergeValue)
                            scale = lerp(1.08f, 0.95f, firstMergeValue)
                            alpha = lerp(1f, 0f, firstMergeValue)
                        }
                        stage.target -> {
                            scale = 1f + 0.05f * firstMergeValue
                            alpha = 1f
                        }
                        stage.remaining -> {
                            x = baseX(stage.remaining)
                        }
                        else -> alpha = 0f
                    }
                }
                is CutStage.SecondReady -> {
                    clickable = true
                    when (index) {
                        stage.combined -> {
                            if (stage.lifted == index) {
                                y = -liftY
                                scale = 1.08f
                            }
                        }
                        stage.remaining -> {
                            if (stage.lifted == index) {
                                y = -liftY
                                scale = 1.08f
                            }
                        }
                        else -> {
                            alpha = 0f
                            clickable = false
                        }
                    }
                }
                is CutStage.SecondMerge -> {
                    clickable = false
                    val start = baseX(stage.source)
                    val end = baseX(stage.target)
                    when (index) {
                        stage.source -> {
                            x = lerp(start, end, secondMergeValue)
                            y = lerp(-liftY, 0f, secondMergeValue)
                            scale = lerp(1.08f, 0.98f, secondMergeValue)
                            alpha = 1f
                        }
                        stage.target -> {
                            x = end
                            scale = 1f + 0.04f * secondMergeValue
                            alpha = 1f
                        }
                        else -> alpha = 0f
                    }
                }
                is CutStage.FinalZoom -> {
                    clickable = false
                    if (index == stage.finalIndex) {
                        x = 0f
                        y = -liftY * zoomValue * 0.3f
                        scale = 1f + zoomValue * 0.12f
                        alpha = 1f
                    } else {
                        alpha = 0f
                    }
                }
                CutStage.Completed -> {
                    clickable = false
                    alpha = 0f
                }
            }

            if (cutStage is CutStage.FirstMerge ||
                cutStage is CutStage.SecondMerge ||
                cutStage is CutStage.FinalZoom
            ) {
                clickable = false
            }

            val offsetXPx = with(density) { (fullWidth * x).toPx() }
            val offsetYPx = with(density) { (pileHeight * y).toPx() }

            return Visual(
                offsetXFraction = offsetXPx,
                offsetYFraction = offsetYPx,
                scale = scale,
                alpha = alpha,
                clickable = clickable && alpha > 0.05f
            )
        }

        (0..2).forEach { index ->
            val visual = visualFor(index)
            if (visual.alpha <= 0f) return@forEach
            Box(
                modifier = Modifier
                    .width(pileWidth)
                    .height(pileHeight)
                    .graphicsLayer {
                        translationX = visual.offsetXFraction
                        translationY = visual.offsetYFraction
                        scaleX = visual.scale
                        scaleY = visual.scale
                        alpha = visual.alpha
                    }
                    .clip(TarotCardShape)
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = visual.clickable,
                        onClick = { handleTap(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                repeat(3) { _ ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp)
                            .clip(TarotCardShape)
                    ) {
                        CardBackArt(
                            modifier = Modifier.fillMaxSize(),
                            overlay = Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0x44000000))
                            ),
                            shape = TarotCardShape,
                            painterOverride = cardBackPainter
                        )
                    }
                }
            }
        }
    }
}

private sealed interface CutStage {
    data object Idle : CutStage
    data class SourceSelected(val source: Int) : CutStage
    data class FirstMerge(
        val source: Int,
        val target: Int,
        val remaining: Int,
        val animationKey: Int
    ) : CutStage
    data class SecondReady(
        val combined: Int,
        val remaining: Int,
        val lifted: Int? = null
    ) : CutStage
    data class SecondMerge(
        val combined: Int,
        val remaining: Int,
        val source: Int,
        val target: Int,
        val animationKey: Int
    ) : CutStage
    data class FinalZoom(
        val finalIndex: Int,
        val animationKey: Int
    ) : CutStage
    data object Completed : CutStage
}

private fun remainingIndex(first: Int, second: Int): Int =
    listOf(0, 1, 2).first { it != first && it != second }
