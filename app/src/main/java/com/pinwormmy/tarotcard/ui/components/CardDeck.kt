package com.pinwormmy.tarotcard.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class ShufflePhase {
    Idle,
    Split,
    Riffle,
    Merge,
    Finished
}

private data class RiffleCardSpec(
    val id: Int,
    val fromLeft: Boolean,
    val depth: Int
)

@Composable
fun CardDeck(
    modifier: Modifier = Modifier,
    width: Dp = 220.dp,
    height: Dp = 320.dp,
    shuffleTrigger: Int = 0,
    onAnimationFinished: (() -> Unit)? = null,
    onPhaseChanged: (ShufflePhase) -> Unit = {}
) {
    val density = LocalDensity.current
    val splitDistancePx = with(density) { 60.dp.toPx() }
    val riffleDropPx = with(density) { 36.dp.toPx() }
    val centerLiftPx = with(density) { 14.dp.toPx() }
    val deckLayers = 5
    val cardsPerSide = 6
    val splitDuration = 360
    val riffleCardDuration = 240
    val riffleStagger = 70L
    val mergeDuration = 420
    val settleDuration = 260

    val cardShape = RoundedCornerShape(20.dp)

    var phase by remember { mutableStateOf(ShufflePhase.Idle) }
    var showRiffleCards by remember { mutableStateOf(false) }

    val leftOffset = remember { Animatable(0f) }
    val rightOffset = remember { Animatable(0f) }
    val centerLift = remember { Animatable(0f) }
    val deckTilt = remember { Animatable(0f) }
    val onFinishedState = rememberUpdatedState(onAnimationFinished)
    val onPhaseChangedState = rememberUpdatedState(onPhaseChanged)

    val riffleCards = remember {
        List(cardsPerSide * 2) { index ->
            RiffleCardSpec(
                id = index,
                fromLeft = index % 2 == 0,
                depth = index / 2
            )
        }
    }
    val riffleProgress = remember(riffleCards) {
        riffleCards.map { Animatable(0f) }
    }

    fun updatePhase(target: ShufflePhase) {
        phase = target
        onPhaseChangedState.value(target)
    }

    LaunchedEffect(shuffleTrigger) {
        if (shuffleTrigger <= 0) return@LaunchedEffect
        updatePhase(ShufflePhase.Split)
        showRiffleCards = false
        leftOffset.snapTo(0f)
        rightOffset.snapTo(0f)
        centerLift.snapTo(0f)
        deckTilt.snapTo(0f)
        riffleProgress.forEach { it.snapTo(0f) }

        leftOffset.animateTo(-splitDistancePx, tween(durationMillis = splitDuration))
        rightOffset.animateTo(splitDistancePx, tween(durationMillis = splitDuration))

        updatePhase(ShufflePhase.Riffle)
        showRiffleCards = true

        coroutineScope {
            launch {
                centerLift.animateTo(-centerLiftPx, tween(durationMillis = splitDuration / 2))
            }
            launch {
                deckTilt.animateTo(-3f, tween(durationMillis = splitDuration / 2))
            }
            riffleProgress.forEachIndexed { index, animatable ->
                launch {
                    delay(riffleStagger * index.toLong())
                    animatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = riffleCardDuration,
                            easing = LinearOutSlowInEasing
                        )
                    )
                }
            }
        }

        updatePhase(ShufflePhase.Merge)
        showRiffleCards = false
        leftOffset.animateTo(0f, tween(durationMillis = mergeDuration, easing = FastOutSlowInEasing))
        rightOffset.animateTo(0f, tween(durationMillis = mergeDuration, easing = FastOutSlowInEasing))
        deckTilt.animateTo(0f, tween(durationMillis = settleDuration))
        centerLift.animateTo(0f, tween(durationMillis = settleDuration, easing = FastOutSlowInEasing))

        updatePhase(ShufflePhase.Finished)
        onFinishedState.value?.invoke()
    }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(cardShape),
        contentAlignment = Alignment.Center
    ) {
        when (phase) {
            ShufflePhase.Idle,
            ShufflePhase.Finished -> {
                DeckStack(
                    cardCount = deckLayers,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    shape = cardShape
                )
            }

            else -> {
                DeckStack(
                    cardCount = deckLayers,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    translationX = leftOffset.value,
                    rotation = deckTiltFromOffset(leftOffset.value, splitDistancePx),
                    shape = cardShape
                )
                DeckStack(
                    cardCount = deckLayers,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    translationX = rightOffset.value,
                    rotation = deckTiltFromOffset(rightOffset.value, splitDistancePx),
                    shape = cardShape
                )
                if (phase != ShufflePhase.Split) {
                    DeckStack(
                        cardCount = deckLayers,
                        modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                        translationY = centerLift.value,
                        rotation = deckTilt.value,
                        shape = cardShape
                    )
                }
            }
        }

        if (showRiffleCards) {
            riffleCards.forEachIndexed { index, card ->
                val progress = riffleProgress[index].value
                if (progress > 0f) {
                    RiffleCard(
                        progress = progress,
                        fromLeft = card.fromLeft,
                        width = width,
                        height = height,
                        translationX = lerp(
                            start = if (card.fromLeft) leftOffset.value else rightOffset.value,
                            stop = 0f,
                            fraction = progress
                        ),
                        translationY = lerp(
                            start = -centerLiftPx - card.depth * 4f,
                            stop = 0f,
                            fraction = progress
                        ) + curveDrop(progress, riffleDropPx),
                        rotation = lerp(
                            start = if (card.fromLeft) -9f else 9f,
                            stop = 0f,
                            fraction = progress
                        )
                    )
                }
            }
        }
    }
}

private fun deckTiltFromOffset(offset: Float, maxOffset: Float): Float {
    if (maxOffset == 0f) return 0f
    val ratio = (offset / maxOffset).coerceIn(-1f, 1f)
    return ratio * 6f
}

private fun curveDrop(progress: Float, dropDistance: Float): Float {
    val arc = abs(progress - 0.5f) * 2f
    return (1f - arc) * -dropDistance * 0.25f
}

@Composable
private fun DeckStack(
    modifier: Modifier = Modifier,
    cardCount: Int,
    translationX: Float = 0f,
    translationY: Float = 0f,
    rotation: Float = 0f,
    shape: Shape = RoundedCornerShape(20.dp)
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                this.translationX = translationX
                this.translationY = translationY
                this.rotationZ = rotation
            },
        contentAlignment = Alignment.Center
    ) {
        repeat(cardCount) { index ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (index * 6).dp)
                    .background(
                        brush = if (index == cardCount - 1) {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF3A3B6D), Color(0xFF1C1D36))
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1B1C33), Color(0xFF0F1024))
                            )
                        },
                        shape = shape
                    )
            )
        }
    }
}

@Composable
private fun RiffleCard(
    progress: Float,
    fromLeft: Boolean,
    width: Dp,
    height: Dp,
    translationX: Float,
    translationY: Float,
    rotation: Float,
    shape: Shape = RoundedCornerShape(20.dp)
) {
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .graphicsLayer {
                this.translationX = translationX
                this.translationY = translationY
                this.rotationZ = rotation
                this.alpha = progress
            }
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (fromLeft) {
                        listOf(Color(0xFF2B2C4A), Color(0xFF13142B))
                    } else {
                        listOf(Color(0xFF34375B), Color(0xFF181933))
                    }
                ),
                shape = shape
            )
    )
}

@Preview
@Composable
private fun CardDeckPreview() {
    CardDeck()
}

@Composable
fun StaticCardDeck(
    modifier: Modifier = Modifier,
    width: Dp = 220.dp,
    height: Dp = 320.dp
) {
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        DeckStack(
            cardCount = 5,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        )
    }
}
