package com.pinwormmy.midoritarot.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pinwormmy.midoritarot.R
import com.pinwormmy.midoritarot.data.DrawHistoryEntry
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.domain.spread.SpreadCatalog
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawHistoryScreen(
    entries: List<DrawHistoryEntry>,
    cardsById: Map<String, TarotCardModel>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEntrySelected: (String) -> Unit,
) {
    BackHandler(onBack = onBack)
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (entries.isEmpty()) {
            EmptyHistory(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = entries, key = { it.id }) { entry ->
                    HistoryEntryCard(
                        entry = entry,
                        cardsById = cardsById,
                        formatter = formatter,
                        onClick = { onEntrySelected(entry.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.history_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun HistoryEntryCard(
    entry: DrawHistoryEntry,
    cardsById: Map<String, TarotCardModel>,
    formatter: DateTimeFormatter,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val spread = remember(entry.spreadType) { SpreadCatalog.find(entry.spreadType) }
    val slotTitles = remember(entry.spreadType) {
        spread.positions.associate { it.slot.id to it.title }
    }
    val dateText = remember(entry.timestampEpochMillis, formatter) {
        val dateTime = Instant.ofEpochMilli(entry.timestampEpochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        dateTime.format(formatter)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .let { base ->
                if (onClick != null) {
                    base.clickable(onClick = onClick)
                } else {
                    base
                }
            },
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = spread.title.resolve(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            )
            if (entry.questionText.isNotBlank()) {
                Text(
                    text = stringResource(id = R.string.reading_question_prefix, entry.questionText),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                entry.cards.forEach { card ->
                    val title = slotTitles[card.slotId]?.resolve()?.takeIf { it.isNotBlank() }
                    val cardName = cardsById[card.cardId]?.name ?: stringResource(id = R.string.history_unknown_card)
                    val reversedSuffix = if (card.isReversed) {
                        " (${stringResource(id = R.string.reversed_title)})"
                    } else {
                        ""
                    }
                    val line = if (title != null) {
                        "$title: $cardName$reversedSuffix"
                    } else {
                        "$cardName$reversedSuffix"
                    }
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.92f),
                    )
                }
            }
        }
    }
}
