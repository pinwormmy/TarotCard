package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.components.CardDeck
import com.pinwormmy.tarotcard.ui.state.SpreadFlowUiState
import com.pinwormmy.tarotcard.ui.state.SpreadPosition
import com.pinwormmy.tarotcard.ui.state.SpreadSlot

@OptIn(ExperimentalFoundationApi::class)
@Composable
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
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "셔플 & 드로우", fontWeight = FontWeight.Bold)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                contentAlignment = Alignment.Center
            ) {
                CardDeck(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onDeckTap),
                    shuffleTrigger = uiState.shuffleTrigger
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onCutRequest
                ) {
                    Text(text = "컷")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onShowGrid
                ) {
                    Text(text = "드로우")
                }
            }

            SelectedCardsRow(
                positions = positions,
                finalCards = uiState.finalCards
            )

            if (uiState.statusMessage != null) {
                Text(text = uiState.statusMessage)
            }

            if (uiState.gridVisible) {
                LazyHorizontalGrid(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    rows = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
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
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "드로우를 누르면 카드가 펼쳐집니다.")
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBack
            ) {
                Text(text = "프리셀렉션으로 돌아가기")
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
private fun SelectedCardsRow(
    positions: List<SpreadPosition>,
    finalCards: Map<SpreadSlot, TarotCardModel>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            positions.forEach { position ->
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = position.title, fontWeight = FontWeight.SemiBold)
                    Text(text = finalCards[position.slot]?.name ?: "-")
                }
            }
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
