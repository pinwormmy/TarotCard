package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SpreadSelectionScreen(
    modifier: Modifier = Modifier,
    onStartPositions: () -> Unit,
    onQuickReading: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Essentials · Past / Present / Future")
            Text(
                text = "과거·현재·미래 3장 스프레드를 선택하면 현재의 흐름을 빠르게 정리할 수 있어요.",
                textAlign = TextAlign.Start
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                CardBackPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "스프레드를 선택하면 각 포지션을 설명하고, 원하는 카드를 미리 지정할 수 있어요."
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onStartPositions
            ) {
                Text(text = "리딩 시작")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onQuickReading
            ) {
                Text(text = "곧바로 리딩하기")
            }
        }
    }
}

@Composable
private fun CardBackPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(220.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF373856), Color(0xFF1F2034))
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 12.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
        )
    }
}
