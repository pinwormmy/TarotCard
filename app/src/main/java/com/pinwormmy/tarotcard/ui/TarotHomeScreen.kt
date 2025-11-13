package com.pinwormmy.tarotcard.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TarotHomeScreen() {
    var isSelecting by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<TarotCard?>(null) }

    if (isSelecting) {
        // 전체 카드 나열 화면
        CardSelectionScreen(
            cards = demoTarotCards,
            onCardSelected = { card ->
                selectedCard = card
                isSelecting = false
                // TODO: 나중에 여기서 선택된 카드 flip 화면으로 연결 가능
            },
            onBack = {
                isSelecting = false
            }
        )
    } else {
        // 기본 홈 화면 (덱 + 뽑기 버튼)
        HomeDeckScreen(
            selectedCard = selectedCard,
            onDrawClick = { isSelecting = true }
        )
    }
}


@Composable
fun DeckView() {
    var isShuffling by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 덱 전체를 좌우로 움직이기 위한 X 오프셋
    val offsetX = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .size(width = 140.dp, height = 200.dp)
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .clickable(enabled = !isShuffling) {
                if (!isShuffling) {
                    isShuffling = true
                    scope.launch {
                        // 좌우로 빠르게 흔들어서 "섞는 느낌" 주기
                        repeat(4) {
                            offsetX.animateTo(
                                targetValue = 20f,
                                animationSpec = tween(durationMillis = 80)
                            )
                            offsetX.animateTo(
                                targetValue = -20f,
                                animationSpec = tween(durationMillis = 80)
                            )
                        }
                        // 원위치
                        offsetX.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = 80)
                        )
                        isShuffling = false
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 카드 여러 장이 포개져 있는 것처럼 살짝씩 아래로 내려서 그리기
        val cardCount = 4
        for (i in 0 until cardCount) {
            SingleDeckCard(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = (i * 4).dp),
                color = if (i == cardCount - 1) Color(0xFF333366) else Color.DarkGray
            )
        }

        Text(
            text = if (isShuffling) "Shuffling..." else "Tarot Deck",
            color = Color.White
        )
    }
}

@Composable
private fun SingleDeckCard(
    modifier: Modifier = Modifier,
    color: Color,
    shape: Shape = RectangleShape
) {
    Box(
        modifier = modifier
            .background(color = color, shape = shape)
    )
}

@Composable
fun HomeDeckScreen(
    selectedCard: TarotCard?,
    onDrawClick: () -> Unit
) {
    androidx.compose.material3.Scaffold { innerPadding ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            // 위쪽: 덱 (공간 대부분 차지)
            Box(
                modifier = Modifier
                    .weight(1f)                 // 위쪽 영역을 크게
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DeckViewLarge()
            }

            // 가운데: 최근에 뽑힌 카드 표시 (있다면)
            if (selectedCard != null) {
                Text(
                    text = "최근에 뽑은 카드: ${selectedCard.name}",
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // 아래: 카드 뽑기 버튼 (가로만 꽉, 높이는 고정)
            androidx.compose.material3.Button(
                onClick = onDrawClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text("카드 뽑기")
            }
        }
    }
}

@Composable
fun DeckViewLarge() {
    DeckView(
        widthDp = 220,
        heightDp = 320
    )
}

@Composable
fun DeckView(
    widthDp: Int = 140,
    heightDp: Int = 200
) {
    var isShuffling by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .size(width = widthDp.dp, height = heightDp.dp)
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .clickable(enabled = !isShuffling) {
                if (!isShuffling) {
                    isShuffling = true
                    scope.launch {
                        repeat(4) {
                            offsetX.animateTo(
                                targetValue = 20f,
                                animationSpec = tween(durationMillis = 80)
                            )
                            offsetX.animateTo(
                                targetValue = -20f,
                                animationSpec = tween(durationMillis = 80)
                            )
                        }
                        offsetX.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = 80)
                        )
                        isShuffling = false
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val cardCount = 4
        for (i in 0 until cardCount) {
            SingleDeckCard(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = (i * 4).dp),
                color = if (i == cardCount - 1) Color(0xFF333366) else Color.DarkGray
            )
        }

        Text(
            text = if (isShuffling) "Shuffling..." else "Tarot Deck",
            color = Color.White
        )
    }
}


