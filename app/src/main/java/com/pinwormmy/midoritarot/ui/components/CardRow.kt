package com.pinwormmy.midoritarot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pinwormmy.midoritarot.domain.model.TarotCardModel

@Suppress("unused")
@Composable
fun CardRow(
    cards: List<TarotCardModel>,
    revealedCardId: String?,
    onCardSelected: (TarotCardModel) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        cards.forEach { card ->
            TarotCard(
                card = card,
                isFaceUp = card.id == revealedCardId,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                onClick = if (enabled && revealedCardId == null) {
                    { onCardSelected(card) }
                } else {
                    null
                }
            )
        }
    }
}
