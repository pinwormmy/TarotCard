package com.pinwormmy.midoritarot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.components.CARD_ASPECT_RATIO
import com.pinwormmy.midoritarot.ui.components.CardFaceArt
import com.pinwormmy.midoritarot.ui.components.applyCardSizeLimit
import com.pinwormmy.midoritarot.ui.components.computeCardSizeLimit
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.pinwormmy.midoritarot.ui.theme.LocalUiHeightScale
import com.pinwormmy.midoritarot.ui.components.windowHeightDp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    card: TarotCardModel?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = card?.name ?: "Card Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                )
            )
        }
    ) { innerPadding ->
        if (card == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Card not found.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            return@Scaffold
        }

        CardDetailBody(card = card, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
private fun CardDetailBody(
    card: TarotCardModel,
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val containerHeightDp = windowHeightDp(windowInfo, density)
    val scale = LocalUiHeightScale.current
    val sizeLimit = computeCardSizeLimit(
        screenHeightDp = containerHeightDp.toInt(),
        scaleFactor = scale,
        heightFraction = 0.7f
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CardFaceArt(
            card = card,
            modifier = Modifier
                .fillMaxWidth()
                .applyCardSizeLimit(sizeLimit)
                .aspectRatio(CARD_ASPECT_RATIO)
        )
        Text(
            text = card.arcana,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = card.name,
            style = MaterialTheme.typography.headlineMedium
        )
        if (card.keywords.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Keywords",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = card.keywords.joinToString(separator = " • "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Section(
            title = "Upright Meaning",
            body = card.uprightMeaning
        )
        Section(
            title = "Reversed Meaning",
            body = card.reversedMeaning
        )
        Section(
            title = "Description",
            body = card.description
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun Section(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
