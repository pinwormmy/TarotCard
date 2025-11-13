package com.pinwormmy.tarotcard.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.ui.TarotCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardSelectionScreen(
    cards: List<TarotCard>,
    onCardSelected: (TarotCard) -> Unit,
    onBack: () -> Unit
) {
    androidx.compose.material3.Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 상단 바: 뒤로가기 + 제목
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onBack) {
                    Text("뒤로")
                }
                Text(
                    text = "카드를 선택하세요",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(64.dp)) // 오른쪽 균형 맞추기용
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    CardSelectionItem(
                        card = card,
                        onClick = { onCardSelected(card) }
                    )
                }
            }
        }
    }
}

@Composable
fun CardSelectionItem(
    card: TarotCard,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(2f / 3f)  // 카드 비율
            .clickable { onClick() }
            .background(Color(0xFF333366)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = card.name,
            color = Color.White,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}
