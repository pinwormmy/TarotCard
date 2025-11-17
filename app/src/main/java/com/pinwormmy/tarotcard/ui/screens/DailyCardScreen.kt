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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.ui.theme.TarotcardTheme

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

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "오늘의 카드") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF16182C), Color(0xFF050711))
                    )
                )
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
                        text = card.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "카드를 두 번 탭하면 설명을 볼 수 있어요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .clickable {
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
                    color = Color.White.copy(alpha = 0.9f)
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isBack) {
                            listOf(Color(0xFF2B2C4E), Color(0xFF14152A))
                        } else {
                            listOf(Color(0xFFE0C097), Color(0xFFC89F63))
                        }
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!isBack) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = card.name,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = card.keywords.joinToString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.25f))
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
                .background(Color(0xFF1E1F36))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = card.name, fontWeight = FontWeight.Bold)
            Text(
                text = if (isReversed) card.reversedMeaning else card.uprightMeaning,
                textAlign = TextAlign.Center
            )
            Text(
                text = "탭해서 닫기",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
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
