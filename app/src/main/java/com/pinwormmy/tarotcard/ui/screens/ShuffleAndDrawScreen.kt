package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.components.CardDeck
import com.pinwormmy.tarotcard.ui.state.SpreadFlowUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShuffleAndDrawScreen(
    uiState: SpreadFlowUiState,
    modifier: Modifier = Modifier,
    onDeckTap: () -> Unit,
    onCut: () -> Unit,
    onDrawToggle: () -> Unit,
    onCardSelected: (TarotCardModel) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Shuffle & Draw", fontWeight = FontWeight.Bold)
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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onCut
            ) {
                Text(text = "컷")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onDrawToggle
            ) {
                Text(text = "드로우")
            }
        }

        if (uiState.statusMessage != null) {
            Text(text = uiState.statusMessage)
        }

        SelectedCardsRow(uiState.drawnCards)

        if (uiState.gridVisible) {
            LazyVerticalGrid(
                modifier = Modifier.weight(1f),
                columns = GridCells.Adaptive(140.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.drawPile, key = { it.id }) { card ->
                    DrawCardItem(card = card, onClick = { onCardSelected(card) })
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "드로우 버튼을 눌러 카드를 펼치세요.")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onBack
            ) {
                Text(text = "뒤로")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onComplete,
                enabled = uiState.drawnCards.size == 3
            ) {
                Text(text = "결과 보기")
            }
        }
    }
}

@Composable
private fun SelectedCardsRow(
    cards: List<TarotCardModel>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val labels = listOf("과거", "현재", "미래")
        labels.forEachIndexed { index, label ->
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = label, fontWeight = FontWeight.SemiBold)
                Text(text = cards.getOrNull(index)?.name ?: "-")
            }
        }
    }
}

@Composable
private fun DrawCardItem(
    card: TarotCardModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2D2F4A), Color(0xFF13142A))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = card.name, fontWeight = FontWeight.Bold)
            Text(
                text = card.arcana,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
