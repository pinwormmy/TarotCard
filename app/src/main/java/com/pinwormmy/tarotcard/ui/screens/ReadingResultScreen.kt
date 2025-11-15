package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.state.SpreadPosition
import com.pinwormmy.tarotcard.ui.state.SpreadSlot

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

    val overlayIndex = revealStates.indexOfFirst {
        it.value == CardRevealPhase.Zoom || it.value == CardRevealPhase.Description
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(text = "리딩 결과", fontWeight = FontWeight.Bold)

        if (orderedCards.all { it == null }) {
            Text(text = "아직 선택된 카드가 없습니다.")
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                orderedCards.forEachIndexed { index, card ->
                    val state = revealStates.getOrNull(index)
                    if (state != null && card != null) {
                        ReadingResultCard(
                            modifier = Modifier.weight(1f),
                            card = card,
                            phase = state.value,
                            onTapped = { state.value = nextPhase(state.value) }
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRestart
        ) {
            Text(text = "새 리딩 시작")
        }
    }

    if (overlayIndex >= 0) {
        val phase = revealStates[overlayIndex].value
        val card = orderedCards.getOrNull(overlayIndex)
        if (card != null) {
            ReadingResultOverlay(
                card = card,
                phase = phase,
                onDismiss = {
                    val state = revealStates[overlayIndex]
                    state.value = nextPhase(state.value)
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
            .clickable { onTapped() },
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
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
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
            if (phase == CardRevealPhase.Description) {
                Text(
                    text = card.description,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "카드를 확대해 살펴보세요.",
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = if (phase == CardRevealPhase.Description) "탭하면 닫습니다" else "한 번 더 탭하면 설명을 확인합니다",
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun nextPhase(current: CardRevealPhase): CardRevealPhase {
    return when (current) {
        CardRevealPhase.Back -> CardRevealPhase.Front
        CardRevealPhase.Front -> CardRevealPhase.Zoom
        CardRevealPhase.Zoom -> CardRevealPhase.Description
        CardRevealPhase.Description -> CardRevealPhase.Front
    }
}
