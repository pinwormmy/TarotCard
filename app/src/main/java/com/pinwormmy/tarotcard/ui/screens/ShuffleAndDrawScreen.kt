package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.components.CardDeck
import com.pinwormmy.tarotcard.ui.state.SpreadFlowUiState
import com.pinwormmy.tarotcard.ui.state.SpreadPosition
import kotlin.random.Random
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun ShuffleAndDrawScreen(
    uiState: SpreadFlowUiState,
    positions: List<SpreadPosition>,
    modifier: Modifier = Modifier,
    onDeckTap: () -> Unit,
    onCutRequest: () -> Unit,
    onCutSelect: (Int) -> Unit,
    onCutCancel: () -> Unit,
    onShowGrid: () -> Unit,
    onCardSelected: (TarotCardModel) -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050711))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    val cardWidth = maxWidth * 0.7f
                    val cardHeight = cardWidth / 0.625f // 세로로 긴 타로카드 비율

                    if (uiState.gridVisible) {
                        DrawPileGrid(
                            cards = uiState.drawPile,
                            modifier = Modifier
                                .fillMaxSize(),
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
                            // 1) 항상 그려지는 "한 덱"
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
                                    onClick = onDeckTap
                                )
                                CardDeck(
                                    modifier = Modifier.fillMaxSize(),
                                    width = cardWidth,
                                    height = cardHeight,
                                    shuffleTrigger = uiState.shuffleTrigger
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(24.dp))
                                        .clickable(onClick = onDeckTap)
                                )
                            }

                            // 2) 컷 모드 3덱 — **cutMode가 true 이거나, 알파가 남아 있을 때만 그리기**
                            if (uiState.cutMode || cutAlpha > 0.01f) {
                                CutModeScene(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = cutAlpha },
                                    fanProgress = fanOutProgress,
                                    onStackChosen = { index ->
                                        onCutSelect(index)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 컷 모드 중에는 statusMessage 숨김
            if (!uiState.cutMode) {
                uiState.statusMessage?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 하단 버튼 Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlineLargeButton(
                    modifier = Modifier.weight(1f),
                    text = if (uiState.cutMode) "취소" else "컷",
                    onClick = {
                        if (uiState.cutMode) onCutCancel() else onCutRequest()
                    }
                )
                OutlineLargeButton(
                    modifier = Modifier.weight(1f),
                    text = "드로우",
                    onClick = onShowGrid
                )
            }
        }
    }
}

@Composable
private fun CardBackImage(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        repeat(3) { offset ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = (offset * 8).dp)
                    .clip(shape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2F3053), Color(0xFF15162B))
                        )
                    )
            )
        }
    }
}

@Composable
private fun OutlineLargeButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier.height(56.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        onClick = onClick
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}


@Composable
private fun DrawPileGrid(
    cards: List<TarotCardModel>,
    modifier: Modifier = Modifier,
    onCardSelected: (TarotCardModel) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        val density = LocalDensity.current
        val columnSpacing = 32.dp
        val cardWidth = (maxWidth - columnSpacing) / 2f
        val cardHeight = cardWidth / 1.6f

        val maxHeightPx = with(density) { maxHeight.toPx() }
        val cardHeightPx = with(density) { cardHeight.toPx() }
        val usableHeightPx = (maxHeightPx - cardHeightPx).coerceAtLeast(0f)

        val leftCount = (cards.size + 1) / 2
        val rightCount = cards.size / 2
        fun rowsInColumn(column: Int) = if (column == 0) leftCount else rightCount

        val columnCenters = listOf(
            -((cardWidth / 2f) + columnSpacing / 2f),
            (cardWidth / 2f) + columnSpacing / 2f
        ).map { with(density) { it.toPx() } }

        val startXPx = with(density) { -maxWidth.toPx() * 0.6f - cardWidth.toPx() }
        val startYPx = with(density) { -maxHeight.toPx() * 0.4f - cardHeight.toPx() }

        cards.forEachIndexed { index, card ->
            val columnIndex = index % 2
            val rowIndex = index / 2
            val rows = rowsInColumn(columnIndex).coerceAtLeast(1)
            val stepPx = if (rows > 1) usableHeightPx / (rows - 1) else 0f
            val targetYPx = stepPx * rowIndex

            val interactionSource = remember(card.id) { MutableInteractionSource() }
            val pressed by interactionSource.collectIsPressedAsState()
            val appear = remember(card.id) { Animatable(0f) }
            val randomRotation = remember(card.id) { (Random.nextFloat() - 0.5f) * 5f }

            val rightColumnCount = rightCount
            val dealOrderIndex = if (columnIndex == 1) rowIndex else rightColumnCount + rowIndex

            LaunchedEffect(card.id) {
                delay(50L * dealOrderIndex)
                appear.animateTo(1f, tween(420))
            }

            val translationX = lerp(startXPx, columnCenters[columnIndex], appear.value)
            val translationY = lerp(startYPx, targetYPx, appear.value)
            val baseScale = lerp(0.9f, 1f, appear.value)
            val pressScale = if (pressed) 1.05f else 1f
            val alpha = appear.value.coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .width(cardWidth)
                    .height(cardHeight)
                    .graphicsLayer {
                        this.translationX = translationX
                        this.translationY = translationY
                        this.alpha = alpha
                        val scale = baseScale * pressScale
                        this.scaleX = scale
                        this.scaleY = scale
                        this.rotationZ = randomRotation
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2F3053), Color(0xFF15162B))
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onCardSelected(card) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF3A3C65),
                                    Color(0xFF1A1B30)
                                )
                            )
                        )
                )
            }
        }
    }
}

/**
 * 컷 모드에서 보여줄 3덱 장면.
 * fanProgress: 0f → 1f 로 갈수록 중앙 덱이 좌/중/우로 퍼져나가는 느낌.
 */
@Composable
private fun CutModeScene(
    modifier: Modifier = Modifier,
    fanProgress: Float,
    onStackChosen: (Int) -> Unit
) {
    val density = LocalDensity.current
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
                        val source = stage.lifted ?: return
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
        val pileHeight = pileWidth / 0.625f
        val baseSpacing = 0.35f
        val twoPileSpacing = 0.2f
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
                            val bop = (1f - firstMergeValue * 0.5f)
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
                            x = -twoPileSpacing
                            if (stage.lifted == index) {
                                y = -liftY
                                scale = 1.08f
                            }
                        }
                        stage.remaining -> {
                            x = twoPileSpacing
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
                    val start = if (stage.source == stage.combined) -twoPileSpacing else twoPileSpacing
                    val end = if (stage.target == stage.combined) -twoPileSpacing else twoPileSpacing
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

            if (cutStage is CutStage.FirstMerge || cutStage is CutStage.SecondMerge || cutStage is CutStage.FinalZoom) {
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
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2F3053), Color(0xFF15162B))
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = visual.clickable,
                        onClick = { handleTap(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                repeat(3) { depth ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = (depth * 4).dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF363966),
                                        Color(0xFF17182D)
                                    )
                                )
                            )
                    )
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
