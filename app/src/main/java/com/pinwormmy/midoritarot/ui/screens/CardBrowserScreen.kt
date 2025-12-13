@file:Suppress("UNUSED_VALUE", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")

package com.pinwormmy.midoritarot.ui.screens

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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pinwormmy.midoritarot.ui.components.CARD_ASPECT_RATIO
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.domain.model.CardCategory
import com.pinwormmy.midoritarot.domain.model.category
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme
import com.pinwormmy.midoritarot.ui.components.CardFaceArt
import com.pinwormmy.midoritarot.ui.components.TarotCardShape
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import com.pinwormmy.midoritarot.ui.theme.TarotUiDefaults
import com.pinwormmy.midoritarot.ui.components.applyCardSizeLimit
import com.pinwormmy.midoritarot.ui.components.CardSizeLimit
import com.pinwormmy.midoritarot.ui.components.rememberCardSizeLimit
import androidx.compose.ui.res.stringResource
import com.pinwormmy.midoritarot.R

private enum class BrowserOverlayPhase { Zoom, Description }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CardBrowserScreen(
    cards: List<TarotCardModel>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val categories = remember { listOf<CardCategory?>(null) + CardCategory.entries.toList() }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedCard by remember { mutableStateOf<TarotCardModel?>(null) }
    var overlayPhase by remember { mutableStateOf(BrowserOverlayPhase.Zoom) }
    val zoomProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val cardSizeLimit = rememberCardSizeLimit(heightFraction = 0.7f)
    @Suppress("UNUSED_VALUE")
    var animateZoom by remember { mutableStateOf(false) }
    var isZoomAnimating by remember { mutableStateOf(false) }
    val filteredCards = remember(selectedTabIndex, cards) {
        val category = categories.getOrNull(selectedTabIndex)
        if (category == null) cards else cards.filter { it.category() == category }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.card_browse_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TarotUiDefaults.topBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {}
            ) {
                categories.forEachIndexed { index, category ->
                    val label = when (category) {
                        null -> stringResource(id = R.string.category_all)
                        CardCategory.MajorArcana -> stringResource(id = R.string.category_major)
                        CardCategory.Wands -> stringResource(id = R.string.category_wands)
                        CardCategory.Cups -> stringResource(id = R.string.category_cups)
                        CardCategory.Swords -> stringResource(id = R.string.category_swords)
                        CardCategory.Pentacles -> stringResource(id = R.string.category_pentacles)
                    }
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
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
                        cardSizeLimit = cardSizeLimit,
                        onClick = {
                            if (isZoomAnimating) return@CardBrowserItem
                            if (overlayPhase == BrowserOverlayPhase.Description) {
                                overlayPhase = BrowserOverlayPhase.Zoom
                                selectedCard = null
                                animateZoom = false
                            } else {
                                selectedCard = card
                                overlayPhase = BrowserOverlayPhase.Zoom
                                animateZoom = true
                            }
                        }
                    )
                }
            }
        }
    }

    val activeCard = selectedCard
    if (activeCard != null) {
        LaunchedEffect(activeCard, animateZoom) {
            if (animateZoom) {
                isZoomAnimating = true
                zoomProgress.snapTo(0f)
                zoomProgress.animateTo(1f, tween(durationMillis = 280))
                isZoomAnimating = false
            } else {
                zoomProgress.snapTo(1f)
            }
        }
        CardDetailOverlay(
            card = activeCard,
            phase = overlayPhase,
            zoomProgress = zoomProgress.value,
            isZoomAnimating = isZoomAnimating,
            cardSizeLimit = cardSizeLimit,
            onCardTap = {
                if (isZoomAnimating) return@CardDetailOverlay
                overlayPhase = when (overlayPhase) {
                    BrowserOverlayPhase.Zoom -> BrowserOverlayPhase.Description
                    BrowserOverlayPhase.Description -> BrowserOverlayPhase.Description
                }
            },
            onDismiss = {
                selectedCard = null
                overlayPhase = BrowserOverlayPhase.Zoom
                isZoomAnimating = false
                coroutineScope.launch { zoomProgress.snapTo(1f) }
            },
            onCloseAll = {
                selectedCard = null
                overlayPhase = BrowserOverlayPhase.Zoom
                isZoomAnimating = false
                coroutineScope.launch { zoomProgress.snapTo(1f) }
            }
        )
    }
}

@Composable
private fun CardBrowserItem(
    card: TarotCardModel,
    cardSizeLimit: CardSizeLimit,
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
                .applyCardSizeLimit(cardSizeLimit)
                .aspectRatio(CARD_ASPECT_RATIO),
            shape = TarotCardShape
        ) {
            CardFaceArt(
                card = card,
                modifier = Modifier.fillMaxSize(),
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
    phase: BrowserOverlayPhase,
    zoomProgress: Float,
    isZoomAnimating: Boolean,
    cardSizeLimit: CardSizeLimit,
    modifier: Modifier = Modifier,
    onCardTap: () -> Unit,
    onDismiss: () -> Unit,
    onCloseAll: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TarotUiDefaults.scrimColor()),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = !isZoomAnimating, onClick = onDismiss)
        )
        if (phase == BrowserOverlayPhase.Zoom) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        val scale = 0.7f + (0.3f * zoomProgress.coerceIn(0f, 1f))
                        scaleX = scale
                        scaleY = scale
                        alpha = zoomProgress.coerceIn(0f, 1f)
                    }
                    .clickable(enabled = !isZoomAnimating, onClick = onCardTap),
                shape = TarotCardShape,
                tonalElevation = 6.dp,
                color = Color.Transparent
            ) {
                CardFaceArt(
                    card = card,
                    modifier = Modifier
                        .fillMaxWidth()
                        .applyCardSizeLimit(cardSizeLimit)
                        .aspectRatio(CARD_ASPECT_RATIO),
                    shape = TarotCardShape
                )
            }
        } else {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clickable(onClick = onCloseAll),
                shape = TarotUiDefaults.panelShape,
                tonalElevation = 8.dp,
                color = TarotUiDefaults.panelColor(),
                border = TarotUiDefaults.panelBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = card.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        text = card.arcana,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (card.keywords.isNotEmpty()) {
                        Text(
                            text = stringResource(
                                id = R.string.keywords_prefix,
                                card.keywords.joinToString(separator = " • ")
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(text = stringResource(id = R.string.upright_title), fontWeight = FontWeight.SemiBold)
                    Text(text = card.uprightMeaning, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                    Text(text = stringResource(id = R.string.reversed_title), fontWeight = FontWeight.SemiBold)
                    Text(text = card.reversedMeaning, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                    if (card.description.isNotBlank()) {
                        Text(
                            text = card.description,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.tap_to_close),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
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
