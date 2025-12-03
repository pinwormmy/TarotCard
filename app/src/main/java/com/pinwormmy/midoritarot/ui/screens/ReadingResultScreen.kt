package com.pinwormmy.midoritarot.ui.screens

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.pinwormmy.midoritarot.data.TarotCardModel
import com.pinwormmy.midoritarot.ui.components.CardFaceArt
import com.pinwormmy.midoritarot.ui.components.SpreadBoard
import com.pinwormmy.midoritarot.ui.components.estimatedBoardHeight
import com.pinwormmy.midoritarot.ui.components.TarotCardShape
import com.pinwormmy.midoritarot.ui.components.CardBackArt
import com.pinwormmy.midoritarot.ui.state.SpreadCardResult
import com.pinwormmy.midoritarot.ui.state.SpreadDefinition
import com.pinwormmy.midoritarot.ui.state.SpreadSlot
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
    val positions = spread.positions
    val orderedSlots = remember(spread) { positions.map { it.slot } }
    val orderedPlacements = orderedSlots.map { cardsBySlot[it] }
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = spread.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (questionText.isNotBlank()) {
                    Text(
                        text = "Q. $questionText",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (orderedPlacements.all { it == null }) {
                Text(text = "아직 선택된 카드가 없습니다.")
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spread.estimatedBoardHeight())
                        .clip(TarotCardShape)
                        .background(Color(0xFF0F1120))
                        .padding(16.dp)
                ) {
                    SpreadBoard(
                        layout = spread.layout,
                        positions = positions,
                        modifier = Modifier.fillMaxSize()
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
                                label = position.title
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
            onClick = onNavigateHome
        ) {
            Text(text = "메인으로")
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
                slotTitle = position.title,
                slotDescription = position.description,
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
private fun ResultPlaceholderCard(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(TarotCardShape)
            .background(Color(0xFF1E2038).copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = label,
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.6f)
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
            .aspectRatio(0.62f)
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
    val dimColor = Color.Black.copy(alpha = 0.7f)
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
                color = Color(0xFFF3F2FF)
            )
            Text(
                text = slotDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE6E5F5),
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
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1F1F2E))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val meaningTitle = if (isReversed) "역방향 해석" else null
                val meaningBody = if (isReversed) card.reversedMeaning else card.uprightMeaning
                Text(
                    text = card.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFF8F8FF)
                )
                if (card.keywords.isNotEmpty()) {
                    Text(
                        text = card.keywords.joinToString(separator = " • "),
                        color = Color(0xFFEAE8FF),
                        textAlign = TextAlign.Center
                    )
                }
                if (meaningTitle != null) {
                    Text(text = meaningTitle, fontWeight = FontWeight.SemiBold, color = Color(0xFFF3F2FF))
                }
                Text(text = meaningBody, textAlign = TextAlign.Center, color = Color(0xFFECEBFF))
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
