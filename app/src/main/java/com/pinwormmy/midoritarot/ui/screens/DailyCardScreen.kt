package com.pinwormmy.midoritarot.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.theme.LocalHapticsEnabled
import com.pinwormmy.midoritarot.ui.theme.HapticsPlayer
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme
import com.pinwormmy.midoritarot.ui.components.CARD_ASPECT_RATIO
import com.pinwormmy.midoritarot.ui.components.CardFaceArt
import com.pinwormmy.midoritarot.ui.components.TarotCardShape
import com.pinwormmy.midoritarot.ui.components.CardBackArt
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCardScreen(
    card: TarotCardModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showFrontImmediately: Boolean = false
) {
    var isBack by remember { mutableStateOf(!showFrontImmediately) }
    var showDescription by remember { mutableStateOf(false) }
    val hapticsEnabled = LocalHapticsEnabled.current
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "오늘의 카드") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (showFrontImmediately) {
                            "앞면이 바로 보입니다. 한 번 탭하면 설명을 볼 수 있어요"
                        } else {
                            "카드를 두 번 탭하면 설명을 볼 수 있어요"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                Box(
                    modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .clickable {
                    if (isBack) {
                        if (hapticsEnabled) {
                            HapticsPlayer.cardFlip(context, hapticFeedback)
                        }
                        isBack = false
                    } else {
                        showDescription = true
                    }
                },
                    contentAlignment = Alignment.Center
                ) {
                    DailyCardDisplay(
                        card = card,
                        isBack = isBack,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = when {
                        isBack -> "첫 탭으로 카드를 뒤집어 보세요"
                        showFrontImmediately -> "탭하면 설명 팝업"
                        else -> "한 번 더 탭하면 설명 팝업"
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                )
            }

            if (showDescription) {
                DailyCardDescriptionSheet(
                    card = card,
                    onDismiss = { showDescription = false }
                )
            }
        }
    }
}

@Composable
private fun DailyCardDisplay(
    card: TarotCardModel,
    isBack: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val rotation by animateFloatAsState(
        targetValue = if (isBack) 180f else 0f,
        label = "dailyCardFlip"
    )
    val faceRotation = 0f
    Surface(
        modifier = modifier.aspectRatio(CARD_ASPECT_RATIO),
        tonalElevation = 12.dp,
        shape = TarotCardShape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12 * density.density
                    rotationZ = faceRotation
                }
                .clip(TarotCardShape)
                .background(Color(0xFF0F0F1A)),
            contentAlignment = Alignment.Center
        ) {
            if (!isBack) {
                CardFaceArt(
                    card = card,
                    modifier = Modifier.fillMaxSize(),
                    shape = TarotCardShape
                )
            } else {
                CardBackArt(
                    modifier = Modifier.fillMaxSize(),
                    overlay = Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0x66000000))
                    ),
                    shape = TarotCardShape
                )
            }
        }
    }
}

@Composable
private fun DailyCardDescriptionSheet(
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = card.name, fontWeight = FontWeight.Bold)
            if (card.keywords.isNotEmpty()) {
                Text(
                    text = card.keywords.joinToString(separator = " • "),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
            Text(
                text = card.uprightMeaning,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "탭해서 닫기",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview
@Composable
private fun DailyCardPreview() {
    TarotcardTheme {
        val sample = TarotCardModel(
            id = "major_the_hermit",
            name = "은둔자",
            arcana = "Major",
            uprightMeaning = "내면의 목소리를 듣기",
            reversedMeaning = "고립에서 벗어나기",
            description = "",
            keywords = listOf("탐색", "침잠")
        )
        DailyCardScreen(
            card = sample,
            onBack = {},
            showFrontImmediately = false
        )
    }
}
