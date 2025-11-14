package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.components.CardDeck
import com.pinwormmy.tarotcard.ui.components.CardRow
import com.pinwormmy.tarotcard.ui.components.ShufflePhase
import com.pinwormmy.tarotcard.ui.components.ShuffleButton
import com.pinwormmy.tarotcard.ui.components.StaticCardDeck
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    cards: List<TarotCardModel>,
    onCardRevealed: (TarotCardModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var shuffleTrigger by remember { mutableIntStateOf(0) }
    var isShuffling by remember { mutableStateOf(false) }
    var revealedCardId by remember { mutableStateOf<String?>(null) }
    var stagedCards by remember { mutableStateOf<List<TarotCardModel>>(emptyList()) }
    var rowEnabled by remember { mutableStateOf(false) }
    var shufflePhase by remember { mutableStateOf(ShufflePhase.Idle) }

    val showCardRow = shufflePhase == ShufflePhase.Finished

    fun startShuffle() {
        if (isShuffling || cards.size < 3) return
        revealedCardId = null
        rowEnabled = false
        stagedCards = cards.shuffled().take(3)
        isShuffling = true
        shufflePhase = ShufflePhase.Split
        shuffleTrigger++
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (shufflePhase == ShufflePhase.Idle) {
                        StaticCardDeck()
                    }
                    CardDeck(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (shufflePhase == ShufflePhase.Idle) 0f else 1f),
                        shuffleTrigger = shuffleTrigger,
                        onAnimationFinished = {
                            isShuffling = false
                            rowEnabled = true
                        },
                        onPhaseChanged = { phase ->
                            shufflePhase = phase
                        }
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Tap Shuffle to draw guidance",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "After the deck settles, choose one of the three hidden cards.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = showCardRow,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CardRow(
                    cards = stagedCards,
                    revealedCardId = revealedCardId,
                    enabled = rowEnabled,
                    onCardSelected = { card ->
                        if (revealedCardId != null) return@CardRow
                        revealedCardId = card.id
                        rowEnabled = false
                        scope.launch {
                            delay(500)
                            onCardRevealed(card)
                        }
                    }
                )
            }

            ShuffleButton(
                onClick = { startShuffle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                enabled = !isShuffling,
                text = if (showCardRow) "Shuffle Again" else "Shuffle"
            )
        }
    }
}
