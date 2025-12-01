package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.theme.LocalTarotSkin
import com.pinwormmy.tarotcard.ui.theme.LocalHapticsEnabled
import com.pinwormmy.tarotcard.ui.theme.TarotcardTheme
import com.pinwormmy.tarotcard.ui.components.CardFaceArt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCardScreen(
    card: TarotCardModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var isBack by remember { mutableStateOf(true) }
    var showDescription by remember { mutableStateOf(false) }
    val isReversed = false
    val skin = LocalTarotSkin.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        modifier = modifier,
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
                .background(skin.backgroundBrush)
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
                    text = "카드를 두 번 탭하면 설명을 볼 수 있어요",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .clickable {
                            if (hapticsEnabled) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            if (isBack) {
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
                        isReversed = isReversed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (isBack) "첫 탭으로 카드를 뒤집어 보세요" else "한 번 더 탭하면 설명 팝업",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                )
            }

            if (showDescription) {
                DailyCardDescriptionSheet(
                    card = card,
                    isReversed = isReversed,
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
    isReversed: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val rotation by animateFloatAsState(
        targetValue = if (isBack) 180f else 0f,
        label = "dailyCardFlip"
    )
    val faceRotation = if (!isBack && isReversed) 180f else 0f
    Surface(
        modifier = modifier.aspectRatio(0.62f),
        tonalElevation = 12.dp,
        shape = RoundedCornerShape(32.dp),
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
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF0F0F1A)),
            contentAlignment = Alignment.Center
        ) {
            if (!isBack) {
                CardFaceArt(
                    card = card,
                    modifier = Modifier.fillMaxSize(),
                    overlay = Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xCC0C0B18))
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun DailyCardDescriptionSheet(
    card: TarotCardModel,
    isReversed: Boolean,
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
            if (isReversed) {
                Text(
                    text = "역방향 해석",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = if (isReversed) card.reversedMeaning else card.uprightMeaning,
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
        DailyCardScreen(card = sample, onBack = {})
    }
}
