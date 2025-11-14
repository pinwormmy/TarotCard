package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.components.CardDeck
import com.pinwormmy.tarotcard.ui.state.SpreadFlowUiState
import com.pinwormmy.tarotcard.ui.state.SpreadPosition

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
                    val cardHeight = cardWidth / 0.625f
                    if (uiState.gridVisible) {
                        LazyHorizontalGrid(
                            modifier = Modifier
                                .width(cardWidth)
                                .height(cardHeight),
                            rows = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            items(uiState.drawPile, key = { it.id }) { card ->
                                DrawGridCard(
                                    card = card,
                                    onSelected = { onCardSelected(card) }
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .width(cardWidth)
                                .height(cardHeight),
                            contentAlignment = Alignment.Center
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
                    }
                }
            }

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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlineLargeButton(
                    modifier = Modifier.weight(1f),
                    text = "컷",
                    onClick = onCutRequest
                )
                OutlineLargeButton(
                    modifier = Modifier.weight(1f),
                    text = "드로우",
                    onClick = onShowGrid
                )
            }
        }

        if (uiState.cutMode) {
            CutOverlay(
                onSelect = onCutSelect,
                onCancel = onCutCancel
            )
        }
    }
}

@Composable
private fun DrawGridCard(
    card: TarotCardModel,
    onSelected: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF34365C), Color(0xFF16172C))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelected
            )
            .graphicsLayer {
                translationY = if (pressed) -8f else 0f
            },
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = card.name, fontWeight = FontWeight.Bold)
            Text(text = card.arcana, color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun CutOverlay(
    onSelect: (Int) -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "스택을 선택해 위로 옮기세요.")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) { index ->
                    MiniDeck(
                        label = "Stack ${index + 1}",
                        onClick = { onSelect(index) }
                    )
                }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                onClick = onCancel
            ) {
                Text(text = "취소")
            }
        }
    }
}

@Composable
private fun MiniDeck(
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 90.dp, height = 140.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2B2D4A), Color(0xFF12132B))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(text = label)
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
