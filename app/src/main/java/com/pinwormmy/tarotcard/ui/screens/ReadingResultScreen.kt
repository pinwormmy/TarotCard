package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.state.SpreadPosition
import com.pinwormmy.tarotcard.ui.state.SpreadSlot
import kotlinx.coroutines.launch
import kotlin.math.min

enum class CardRevealPhase {
    Back,
    Front,
    Zoom,
    Description
}

@Composable
fun ReadingResultScreen(
    positions: List<SpreadPosition>,
    cardsBySlot: Map<SpreadSlot, TarotCardModel>,
    modifier: Modifier = Modifier,
    onRestart: () -> Unit
) {
    val orderedSlots = remember(positions) { positions.map { it.slot } }
    val orderedCards = orderedSlots.map { cardsBySlot[it] }
    val revealStates = remember(orderedCards) {
        orderedCards.map { mutableStateOf(CardRevealPhase.Back) }
    }
    val cardBounds = remember(orderedCards.size) {
        List(orderedCards.size) { mutableStateOf<Rect?>(null) }
    }
    val containerBounds = remember { mutableStateOf<Rect?>(null) }
    var zoomedIndex by remember { mutableStateOf<Int?>(null) }
    val zoomAnimation = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    fun openZoom(index: Int) {
        if (zoomedIndex == index) return
        zoomedIndex = index
        coroutineScope.launch {
            zoomAnimation.snapTo(0f)
            zoomAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
    }

    fun closeZoom() {
        val target = zoomedIndex ?: return
        coroutineScope.launch {
            zoomAnimation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
            zoomedIndex = null
            revealStates[target].value = CardRevealPhase.Front
        }
    }

    fun showDescription(index: Int) {
        revealStates[index].value = CardRevealPhase.Description
    }

    fun dismissDescription(index: Int) {
        revealStates[index].value = CardRevealPhase.Zoom
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .onGloballyPositioned { containerBounds.value = it.boundsInRoot() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (orderedCards.all { it == null }) {
                Text(text = "아직 선택된 카드가 없습니다.")
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    orderedCards.forEachIndexed { index, card ->
                        val state = revealStates.getOrNull(index)
                        if (state != null && card != null) {
                            val boundsState = cardBounds[index]
                            ReadingResultCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .onGloballyPositioned { coordinates ->
                                        boundsState.value = coordinates.boundsInRoot()
                                    },
                                card = card,
                                phase = state.value,
                                enabled = zoomedIndex == null,
                                onTapped = {
                                    if (zoomedIndex != null) {
                                        return@ReadingResultCard
                                    }
                                    when (state.value) {
                                        CardRevealPhase.Back -> state.value = CardRevealPhase.Front
                                        CardRevealPhase.Front -> {
                                            state.value = CardRevealPhase.Zoom
                                            openZoom(index)
                                        }
                                        CardRevealPhase.Zoom -> showDescription(index)
                                        CardRevealPhase.Description -> Unit
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onClick = onRestart
        ) {
            Text(text = "새 리딩 시작")
        }
    }

    val activeZoomIndex = zoomedIndex
    if (activeZoomIndex != null) {
        val phase = revealStates.getOrNull(activeZoomIndex)?.value
        val card = orderedCards.getOrNull(activeZoomIndex)
        val originBounds = cardBounds.getOrNull(activeZoomIndex)?.value
        val container = containerBounds.value
        if (card != null && phase != null) {
            ReadingResultOverlay(
                card = card,
                phase = phase,
                zoomProgress = zoomAnimation.value,
                cardBounds = originBounds,
                containerBounds = container,
                onCardTapped = {
                    if (phase == CardRevealPhase.Zoom) {
                        showDescription(activeZoomIndex)
                    }
                },
                onBackgroundTapped = {
                    if (phase == CardRevealPhase.Zoom) {
                        closeZoom()
                    } else if (phase == CardRevealPhase.Description) {
                        dismissDescription(activeZoomIndex)
                    }
                },
                onDescriptionDismiss = {
                    dismissDescription(activeZoomIndex)
                }
            )
        }
    }
}

@Composable
private fun ReadingResultCard(
    card: TarotCardModel,
    phase: CardRevealPhase,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTapped: () -> Unit
) {
    val isBack = phase == CardRevealPhase.Back
    val rotation by animateFloatAsState(
        targetValue = if (isBack) 180f else 0f,
        label = "cardFlip"
    )
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .aspectRatio(0.62f)
            .clip(RoundedCornerShape(24.dp))
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density.density
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isBack) {
                        listOf(Color(0xFF35365A), Color(0xFF191A2F))
                    } else {
                        listOf(Color(0xFFE0C097), Color(0xFFC89F63))
                    }
                )
            )
            .clickable(enabled = enabled) { onTapped() },
        contentAlignment = Alignment.Center
    ) {
        if (!isBack) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = card.name, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(
                    text = card.keywords.joinToString(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
private fun ReadingResultOverlay(
    card: TarotCardModel,
    phase: CardRevealPhase,
    zoomProgress: Float,
    cardBounds: Rect?,
    containerBounds: Rect?,
    onCardTapped: () -> Unit,
    onBackgroundTapped: () -> Unit,
    onDescriptionDismiss: () -> Unit
) {
    val dimColor = Color.Black.copy(alpha = 0.7f)
    val density = LocalDensity.current
    val backgroundInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dimColor),
        contentAlignment = Alignment.Center
    ) {
        if (phase == CardRevealPhase.Description) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = backgroundInteraction,
                        indication = null
                    ) { onDescriptionDismiss() }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1F1F2E))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = card.name, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(
                    text = card.description,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "탭하면 닫습니다",
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val aspectRatio = 0.62f
            val targetWidthPx = containerBounds?.let { container ->
                val widthLimit = container.width * 0.7f
                val heightLimit = container.height * 0.8f
                val widthFromHeight = heightLimit * aspectRatio
                min(widthLimit, widthFromHeight)
            }
            val targetWidthDp = targetWidthPx?.let { with(density) { it.toDp() } } ?: 280.dp
            val startWidthPx = cardBounds?.width
            val startScale = if (targetWidthPx != null && startWidthPx != null && targetWidthPx > 0f) {
                (startWidthPx / targetWidthPx).coerceIn(0.3f, 1f)
            } else {
                0.6f
            }
            val startOffset = if (cardBounds != null && containerBounds != null) {
                Offset(
                    cardBounds.center.x - containerBounds.center.x,
                    cardBounds.center.y - containerBounds.center.y
                )
            } else {
                Offset.Zero
            }
            val easedProgress = FastOutSlowInEasing.transform(zoomProgress.coerceIn(0f, 1f))
            val offsetX = lerp(startOffset.x, 0f, easedProgress)
            val offsetY = lerp(startOffset.y, 0f, easedProgress)
            val scale = lerp(startScale, 1f, easedProgress)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = backgroundInteraction,
                        indication = null
                    ) { onBackgroundTapped() }
            )

            ReadingResultCard(
                card = card,
                phase = phase,
                modifier = Modifier
                    .width(targetWidthDp)
                    .graphicsLayer {
                        translationX = offsetX
                        translationY = offsetY
                        scaleX = scale
                        scaleY = scale
                    },
                onTapped = onCardTapped
            )
        }
    }
}

private fun nextPhase(current: CardRevealPhase): CardRevealPhase {
    return when (current) {
        CardRevealPhase.Back -> CardRevealPhase.Front
        CardRevealPhase.Front -> CardRevealPhase.Zoom
        CardRevealPhase.Zoom -> CardRevealPhase.Description
        CardRevealPhase.Description -> CardRevealPhase.Zoom
    }
}
