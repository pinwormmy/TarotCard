package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.state.SpreadPosition

@Composable
fun PositionSelectScreen(
    positions: List<SpreadPosition>,
    currentIndex: Int,
    selectedCards: List<TarotCardModel?>,
    modifier: Modifier = Modifier,
    onPickCard: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onContinue: () -> Unit,
    canContinue: Boolean = false
) {
    val currentPosition = positions.getOrNull(currentIndex)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "포지션 ${currentIndex + 1} / ${positions.size}",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentPosition?.title.orEmpty(),
                fontWeight = FontWeight.Bold
            )
            Text(text = currentPosition?.description.orEmpty())
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            selectedCards.forEachIndexed { index, card ->
                PositionCardItem(
                    title = positions.getOrNull(index)?.title ?: "",
                    subtitle = positions.getOrNull(index)?.description ?: "",
                    selectedCardName = card?.name
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPickCard
            ) {
                Text(text = "카드 선택하기")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onPrevious,
                    enabled = currentIndex > 0
                ) {
                    Text(text = "이전")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onNext,
                    enabled = currentIndex < positions.lastIndex
                ) {
                    Text(text = "다음")
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onContinue,
                enabled = canContinue
            ) {
                Text(text = "리딩 준비하기")
            }
        }
    }
}

@Composable
private fun PositionCardItem(
    title: String,
    subtitle: String,
    selectedCardName: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = Color.White.copy(alpha = 0.7f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = selectedCardName ?: "아직 선택되지 않았습니다",
                textAlign = TextAlign.Center
            )
        }
    }
}
