package com.pinwormmy.midoritarot.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.domain.spread.SpreadDefinition
import com.pinwormmy.midoritarot.domain.spread.SpreadSlot
import com.pinwormmy.midoritarot.domain.spread.SpreadType
import com.pinwormmy.midoritarot.ui.components.CARD_ASPECT_RATIO
import com.pinwormmy.midoritarot.ui.components.CardBackArt
import com.pinwormmy.midoritarot.ui.components.CardFaceArt
import com.pinwormmy.midoritarot.ui.components.SpreadBoard
import com.pinwormmy.midoritarot.ui.components.TarotCardShape
import com.pinwormmy.midoritarot.ui.components.computeCardSizeLimit
import com.pinwormmy.midoritarot.ui.components.applyCardSizeLimit
import com.pinwormmy.midoritarot.ui.theme.LocalUiHeightScale
import com.pinwormmy.midoritarot.ui.theme.TarotUiDefaults
import com.pinwormmy.midoritarot.ui.components.windowHeightDp
import com.pinwormmy.midoritarot.ui.state.SpreadCardResult
import com.pinwormmy.midoritarot.R
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
    spread: SpreadDefinition,
    cardsBySlot: Map<SpreadSlot, SpreadCardResult>,
    questionText: String,
    modifier: Modifier = Modifier,
    onNavigateHome: () -> Unit
) {
    val rememberedCards = remember { mutableStateOf(cardsBySlot) }
    if (cardsBySlot.isNotEmpty()) {
        rememberedCards.value = cardsBySlot
    }
    val displayCards = rememberedCards.value

    val positions = spread.positions
    val orderedSlots = remember(spread) { positions.map { it.slot } }
    val orderedPlacements = orderedSlots.map { displayCards[it] }
    val revealStates = remember(spread) {
        positions.map { mutableStateOf(CardRevealPhase.Back) }
    }
    val cardBounds = remember(spread) {
        List(positions.size) { mutableStateOf<Rect?>(null) }
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

    fun dismissDescriptionAndClose(index: Int) {
        dismissDescription(index)
        closeZoom()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 2.dp, vertical = 6.dp)
            .onGloballyPositioned { containerBounds.value = it.boundsInRoot() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = spread.title.resolve(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (questionText.isNotBlank()) {
                    Text(
                        text = stringResource(
                            id = R.string.reading_question_prefix,
                            questionText
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TarotUiDefaults.hint(0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (orderedPlacements.all { it == null }) {
                    Text(text = stringResource(id = R.string.reading_result_empty))
                } else {
                    val boardSpacing = when (spread.type) {
                        SpreadType.CelticCross -> 2.dp
                        else -> 4.dp
                    }
                    SpreadBoard(
                        layout = spread.layout,
                        positions = positions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        spacing = boardSpacing
                    ) { index, position, cardModifier ->
                        val state = revealStates.getOrNull(index)
                        val placement = orderedPlacements.getOrNull(index)
                        val card = placement?.card
                        val isReversed = placement?.isReversed == true
                        val boundsState = cardBounds.getOrNull(index)
                        if (state != null && card != null && boundsState != null) {
                            val measuredModifier = cardModifier.onGloballyPositioned { coordinates ->
                                boundsState.value = coordinates.boundsInRoot()
                            }
                            ReadingResultCard(
                                modifier = measuredModifier,
                                card = card,
                                isReversed = isReversed,
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
                        } else {
                            ResultPlaceholderCard(
                                modifier = cardModifier,
                                label = position.title.resolve()
                            )
                        }
                    }
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                onClick = onNavigateHome
            ) {
                Text(text = stringResource(id = R.string.reading_result_home))
            }
        }
    }

    val activeZoomIndex = zoomedIndex
    if (activeZoomIndex != null) {
        val phase = revealStates.getOrNull(activeZoomIndex)?.value
        val placement = orderedPlacements.getOrNull(activeZoomIndex)
        val card = placement?.card
        val isReversed = placement?.isReversed == true
        val position = positions.getOrNull(activeZoomIndex)
        val originBounds = cardBounds.getOrNull(activeZoomIndex)?.value
        val container = containerBounds.value
        if (card != null && phase != null && position != null) {
            ReadingResultOverlay(
                card = card,
                isReversed = isReversed,
                phase = phase,
                zoomProgress = zoomAnimation.value,
                cardBounds = originBounds,
                containerBounds = container,
                slotTitle = position.title.resolve(),
                slotDescription = position.description.resolve(),
                slotOrder = position.order,
                onCardTapped = {
                    if (phase == CardRevealPhase.Zoom) {
                        showDescription(activeZoomIndex)
                    }
                },
                onBackgroundTapped = {
                    if (phase == CardRevealPhase.Zoom) {
                        closeZoom()
                    } else if (phase == CardRevealPhase.Description) {
                        dismissDescriptionAndClose(activeZoomIndex)
                    }
                },
                onDescriptionDismiss = {
                    dismissDescriptionAndClose(activeZoomIndex)
                }
            )
        }
    }
}

@Composable
private fun ResultPlaceholderCard(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(TarotCardShape)
            .background(TarotUiDefaults.secondaryPanelColor(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = label,
            textAlign = TextAlign.Center,
            color = TarotUiDefaults.hint(0.7f)
        )
    }
}

@Composable
private fun ReadingResultCard(
    card: TarotCardModel,
    isReversed: Boolean,
    phase: CardRevealPhase,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTapped: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val uiScale = LocalUiHeightScale.current
    val sizeLimit = computeCardSizeLimit(
        screenHeightDp = windowHeightDp(windowInfo, LocalDensity.current).toInt(),
        scaleFactor = uiScale,
        heightFraction = 0.7f
    )
    val isBack = phase == CardRevealPhase.Back
    val rotation by animateFloatAsState(
        targetValue = if (isBack) 180f else 0f,
        label = "cardFlip"
    )
    val density = LocalDensity.current
    val faceRotation = if (!isBack && isReversed) 180f else 0f
    val shape = TarotCardShape
    Box(
        modifier = modifier
            .applyCardSizeLimit(sizeLimit)
            .aspectRatio(CARD_ASPECT_RATIO)
            .clip(shape)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density.density
                rotationZ = faceRotation
            }
            .clickable(enabled = enabled) { onTapped() },
        contentAlignment = Alignment.Center
    ) {
        if (!isBack) {
            CardFaceArt(
                card = card,
                modifier = Modifier.fillMaxSize(),
                shape = shape
            )
        } else {
            CardBackArt(
                modifier = Modifier.fillMaxSize(),
                overlay = Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0x66000000))
                ),
                shape = shape
            )
        }
    }
}

@Composable
private fun ReadingResultOverlay(
    card: TarotCardModel,
    isReversed: Boolean,
    phase: CardRevealPhase,
    zoomProgress: Float,
    cardBounds: Rect?,
    containerBounds: Rect?,
    slotTitle: String,
    slotDescription: String,
    slotOrder: Int,
    onCardTapped: () -> Unit,
    onBackgroundTapped: () -> Unit,
    onDescriptionDismiss: () -> Unit
) {
    val dimColor = TarotUiDefaults.scrimColor()
    val density = LocalDensity.current
    val backgroundInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dimColor),
        contentAlignment = Alignment.Center
    ) {
        val headerTitle = "$slotOrder. $slotTitle"
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = headerTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = slotDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = TarotUiDefaults.hint(0.85f),
                textAlign = TextAlign.Center
            )
        }

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
                    .clip(TarotUiDefaults.sheetShape)
                    .background(TarotUiDefaults.panelColor(), TarotUiDefaults.sheetShape)
                    .border(TarotUiDefaults.panelBorder(), TarotUiDefaults.sheetShape)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val meaningTitle = if (isReversed) stringResource(id = R.string.reading_result_reversed) else null
                val meaningBody = if (isReversed) card.reversedMeaning else card.uprightMeaning
                Text(
                    text = card.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (card.keywords.isNotEmpty()) {
                    Text(
                        text = card.keywords.joinToString(separator = " • "),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                if (meaningTitle != null) {
                    Text(
                        text = meaningTitle,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = meaningBody,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.reading_result_close_hint),
                    color = TarotUiDefaults.hint(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val aspectRatio = CARD_ASPECT_RATIO
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

            if (phase == CardRevealPhase.Zoom) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 96.dp)
                        .padding(horizontal = 24.dp),
                    text = stringResource(id = R.string.reading_result_touch_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = TarotUiDefaults.hint(0.82f)
                )
            }

            ReadingResultCard(
                card = card,
                isReversed = isReversed,
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
