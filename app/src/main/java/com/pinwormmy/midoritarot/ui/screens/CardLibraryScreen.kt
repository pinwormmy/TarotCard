package com.pinwormmy.midoritarot.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.state.CardCategory
import com.pinwormmy.midoritarot.ui.state.category
import com.pinwormmy.midoritarot.ui.components.CARD_ASPECT_RATIO
import com.pinwormmy.midoritarot.ui.components.CardFaceArt
import com.pinwormmy.midoritarot.ui.components.TarotCardShape
import com.pinwormmy.midoritarot.ui.components.applyCardSizeLimit
import com.pinwormmy.midoritarot.ui.components.computeCardSizeLimit
import com.pinwormmy.midoritarot.ui.components.CardSizeLimit
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import com.pinwormmy.midoritarot.ui.theme.LocalUiHeightScale
import com.pinwormmy.midoritarot.ui.components.windowHeightDp
import androidx.compose.ui.res.stringResource
import com.pinwormmy.midoritarot.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("unused")
fun CardLibraryScreen(
    cards: List<TarotCardModel>,
    selectedCategory: CardCategory,
    targetSlotTitle: String,
    modifier: Modifier = Modifier,
    onCategoryChange: (CardCategory) -> Unit,
    onCardSelected: (TarotCardModel) -> Unit,
    onBack: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val uiScale = LocalUiHeightScale.current
    val containerHeightDp = windowHeightDp(windowInfo, density)
    val cardSizeLimit = computeCardSizeLimit(
        screenHeightDp = containerHeightDp.toInt(),
        scaleFactor = uiScale,
        heightFraction = 0.7f
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.card_library_title, targetSlotTitle),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.card_library_subtitle),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        val categories = CardCategory.entries
        TabRow(selectedTabIndex = categories.indexOf(selectedCategory)) {
            categories.forEach { category ->
                Tab(
                    selected = selectedCategory == category,
                    onClick = { onCategoryChange(category) },
                    text = { Text(text = category.displayName) }
                )
            }
        }

        val filtered = remember(cards, selectedCategory) {
            cards.filter { it.category() == selectedCategory }
        }

        LazyVerticalGrid(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp),
            columns = GridCells.Adaptive(140.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered, key = { it.id }) { card ->
                CardLibraryItem(
                    card = card,
                    onCardSelected = onCardSelected,
                    cardSizeLimit = cardSizeLimit
                )
            }
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = onBack
        ) {
            Text(text = stringResource(id = R.string.card_library_back))
        }
    }
}

@Composable
private fun CardLibraryItem(
    card: TarotCardModel,
    onCardSelected: (TarotCardModel) -> Unit,
    modifier: Modifier = Modifier,
    cardSizeLimit: CardSizeLimit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardSelected(card) },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        val shape: Shape = TarotCardShape
        Column(
            modifier = Modifier
                .background(Color.Transparent, shape = shape)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardFaceArt(
                card = card,
                modifier = Modifier
                    .fillMaxWidth()
                    .applyCardSizeLimit(cardSizeLimit)
                    .aspectRatio(CARD_ASPECT_RATIO),
                shape = shape
            )
            Text(text = card.name, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(
                text = card.keywords.take(3).joinToString(),
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.card_library_select),
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
