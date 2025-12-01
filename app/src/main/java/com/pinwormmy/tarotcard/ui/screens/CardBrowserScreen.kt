package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.state.CardCategory
import com.pinwormmy.tarotcard.ui.state.category
import com.pinwormmy.tarotcard.ui.theme.TarotcardTheme
import com.pinwormmy.tarotcard.ui.components.CardFaceArt
import com.pinwormmy.tarotcard.ui.components.TarotCardShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CardBrowserScreen(
    cards: List<TarotCardModel>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val categories = remember { listOf<CardCategory?>(null) + CardCategory.values().toList() }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedCard by remember { mutableStateOf<TarotCardModel?>(null) }
    val filteredCards = remember(selectedTabIndex, cards) {
        val category = categories.getOrNull(selectedTabIndex)
        if (category == null) cards else cards.filter { it.category() == category }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "카드 살펴보기") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                divider = {}
            ) {
                categories.forEachIndexed { index, category ->
                    val label = when (category) {
                        null -> "전체"
                        CardCategory.MajorArcana -> "메이저"
                        CardCategory.Wands -> "완즈"
                        CardCategory.Cups -> "컵"
                        CardCategory.Swords -> "소드"
                        CardCategory.Pentacles -> "펜타클"
                    }
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = label) }
                    )
                }
            }

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCards, key = { it.id }) { card ->
                    CardBrowserItem(
                        card = card,
                        onClick = { selectedCard = card }
                    )
                }
            }
        }
    }

    val activeCard = selectedCard
    if (activeCard != null) {
        CardDetailOverlay(card = activeCard, onDismiss = { selectedCard = null })
    }
}

@Composable
private fun CardBrowserItem(
    card: TarotCardModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.62f),
            shape = TarotCardShape
        ) {
            CardFaceArt(
                card = card,
                modifier = Modifier.fillMaxSize(),
                overlay = Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xCC0F0F1F))
                ),
                shape = TarotCardShape
            )
        }
        Text(
            text = card.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CardDetailOverlay(
    card: TarotCardModel,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clickable(onClick = onDismiss),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CardFaceArt(
                    card = card,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.62f)
                )
                Text(text = card.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                if (card.description.isNotBlank()) {
                    Text(
                        text = card.description,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }
                if (card.keywords.isNotEmpty()) {
                    Text(
                        text = "키워드",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = card.keywords.joinToString(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
                Text(text = "정방향", fontWeight = FontWeight.SemiBold)
                Text(text = card.uprightMeaning, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                Text(text = "역방향", fontWeight = FontWeight.SemiBold)
                Text(text = card.reversedMeaning, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                Text(text = "탭하면 닫힙니다", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview
@Composable
private fun CardBrowserPreview() {
    val sample = listOf(
        TarotCardModel(
            id = "major_the_fool",
            name = "바보",
            arcana = "Major",
            uprightMeaning = "새로운 시작",
            reversedMeaning = "무모함",
            description = "",
            keywords = listOf("시작")
        )
    )
    TarotcardTheme {
        CardBrowserScreen(cards = sample, onBack = {})
    }
}
