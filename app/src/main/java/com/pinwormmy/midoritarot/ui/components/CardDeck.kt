package com.pinwormmy.midoritarot.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.painter.Painter
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
    painter: Painter? = null,
    onAnimationFinished: (() -> Unit)? = null,
    onPhaseChanged: (ShufflePhase) -> Unit = {},
    onRiffleBeat: () -> Unit = {}
) {
    val density = LocalDensity.current
    val backPainter = painter ?: rememberCardBackPainter()
    val splitDistanceDp = maxOf(120.dp, width * 0.9f)
    val splitDistancePx = with(density) { splitDistanceDp.toPx() }
    val riffleDropPx = with(density) { 36.dp.toPx() }
    val centerLiftPx = with(density) { 14.dp.toPx() }
    val deckLayers = 5
    val cardsPerSide = 6
    val splitDuration = 360
    val riffleCardDuration = 240
    val riffleStagger = 70L
    val mergeDuration = 420
    val settleDuration = 260

    val cardShape = TarotCardShape

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
                    onRiffleBeat()
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

    val deckSizeModifier = Modifier.size(width = width, height = height)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        when (phase) {
            ShufflePhase.Idle,
            ShufflePhase.Finished -> {
                DeckStack(
                    cardCount = deckLayers,
                    modifier = Modifier
                        .then(deckSizeModifier)
                        .align(Alignment.Center),
                    shape = cardShape,
                    painter = backPainter
                )
            }

            else -> {
                DeckStack(
                    cardCount = deckLayers,
                    modifier = Modifier
                        .then(deckSizeModifier)
                        .align(Alignment.Center),
                    translationX = leftOffset.value,
                    rotation = deckTiltFromOffset(leftOffset.value, splitDistancePx),
                    shape = cardShape,
                    painter = backPainter
                )
                DeckStack(
                    cardCount = deckLayers,
                    modifier = Modifier
                        .then(deckSizeModifier)
                        .align(Alignment.Center),
                    translationX = rightOffset.value,
                    rotation = deckTiltFromOffset(rightOffset.value, splitDistancePx),
                    shape = cardShape,
                    painter = backPainter
                )
                if (phase != ShufflePhase.Split) {
                    DeckStack(
                        cardCount = deckLayers,
                        modifier = Modifier
                            .then(deckSizeModifier)
                            .align(Alignment.Center),
                        translationY = centerLift.value,
                        rotation = deckTilt.value,
                        shape = cardShape,
                        painter = backPainter
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
                        ),
                        painter = backPainter
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
    shape: Shape = TarotCardShape,
    painter: Painter? = null
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                this.translationX = translationX
                this.translationY = translationY
                this.rotationZ = rotation
            },
        contentAlignment = Alignment.Center
    ) {
        repeat(cardCount) { index ->
            val depthAlpha = 0.25f + (index / cardCount.toFloat()) * 0.25f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (index * 6).dp)
                    .clip(shape)
            ) {
                CardBackArt(
                    modifier = Modifier.fillMaxSize(),
                    overlay = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f * depthAlpha)
                        )
                    ),
                    shape = shape,
                    painterOverride = painter
                )
            }
        }
    }
}

@Composable
private fun RiffleCard(
    progress: Float,
    width: Dp,
    height: Dp,
    translationX: Float,
    translationY: Float,
    rotation: Float,
    shape: Shape = TarotCardShape,
    painter: Painter? = null
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
    ) {
        CardBackArt(
            modifier = Modifier.fillMaxSize(),
            overlay = Brush.verticalGradient(
                listOf(Color.Transparent, Color(0x44000000))
            ),
            shape = shape,
            painterOverride = painter
        )
    }
}

@Preview
@Composable
private fun CardDeckPreview() {
    CardDeck()
}

@Composable
@Suppress("unused")
fun StaticCardDeck(
    modifier: Modifier = Modifier,
    width: Dp = 220.dp,
    height: Dp = 320.dp
) {
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(TarotCardShape),
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
