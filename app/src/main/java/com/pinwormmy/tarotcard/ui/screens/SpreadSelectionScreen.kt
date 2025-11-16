package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.pinwormmy.tarotcard.ui.state.SpreadPosition
import com.pinwormmy.tarotcard.ui.state.SpreadPreselectionState
import com.pinwormmy.tarotcard.ui.state.SpreadSlot

@Composable
fun SpreadSelectionScreen(
    positions: List<SpreadPosition>,
    preselectionState: SpreadPreselectionState,
    modifier: Modifier = Modifier,
    onPickCard: (SpreadSlot) -> Unit,
    onStartReading: () -> Unit
) {
    var activeSlot by remember { mutableStateOf<SpreadSlot?>(null) }
    val selectedCount = preselectionState.selectedCount
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "과거 / 현재 / 미래", fontWeight = FontWeight.Bold)
            Text(text = "필요한 카드를 미리 고를 수 있어요. 선택하지 않은 포지션은 다음 단계에서 드로우 됩니다.")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            positions.forEach { position ->
                SpreadBackCard(
                    modifier = Modifier.weight(1f),
                    label = position.title,
                    isActive = activeSlot == position.slot,
                    onClick = {
                        activeSlot = if (activeSlot == position.slot) null else position.slot
                    }
                )
            }
        }

        if (activeSlot == null) {
            DefaultDescription()
        } else {
            val position = positions.firstOrNull { it.slot == activeSlot }
            if (position != null) {
                PositionPanel(
                    position = position,
                    selectedCardName = preselectionState.get(position.slot)?.name,
                    onClose = { activeSlot = null },
                    onPickCard = {
                        onPickCard(position.slot)
                        activeSlot = null
                    }
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onStartReading
            ) {
                Text(text = "리딩 시작하기")
            }
        }
    }
}

@Composable
private fun SpreadBackCard(
    label: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .aspectRatio(0.62f)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3C3D63), Color(0xFF1A1B30))
                )
            )
            .border(
                width = 2.dp,
                color = if (isActive) Color(0xFFB3A4FF) else Color.Transparent,
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DefaultDescription() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f)
    ) {
        Text(
            modifier = Modifier.padding(20.dp),
            text = "이 스프레드는 과거·현재·미래의 흐름을 살펴보기 위한 3장 스프레드입니다.\n미리 카드를 선택하거나 셔플을 통해 무작위로 뽑을 수 있어요.",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PositionPanel(
    position: SpreadPosition,
    selectedCardName: String?,
    onClose: () -> Unit,
    onPickCard: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = position.title, fontWeight = FontWeight.Bold)
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close panel")
                }
            }
            Text(text = position.description)
            if (selectedCardName != null) {
                Text(
                    text = "선택된 카드: $selectedCardName",
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPickCard
            ) {
                Text(text = "카드 선택하기")
            }
        }
    }
}
