package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.ui.theme.TarotcardTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpreadMenuScreen(
    onPastPresentFuture: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spreads = listOf(
        SpreadMenuEntry(
            title = "원카드",
            description = "가볍게 오늘의 방향성을 확인",
            enabled = false,
            onClick = {}
        ),
        SpreadMenuEntry(
            title = "과거, 현재, 미래",
            description = "이미 구현된 3장 스프레드",
            enabled = true,
            onClick = onPastPresentFuture
        ),
        SpreadMenuEntry(
            title = "에너지와 조언",
            description = "현재 에너지를 정리하고 힌트를 받아요",
            enabled = false,
            onClick = {}
        ),
        SpreadMenuEntry(
            title = "앞으로 나아갈 길",
            description = "장기 계획과 여정 진단",
            enabled = false,
            onClick = {}
        ),
        SpreadMenuEntry(
            title = "관계",
            description = "상대방과의 연결을 조망",
            enabled = false,
            onClick = {}
        ),
        SpreadMenuEntry(
            title = "켈틱 크로스",
            description = "깊이 있는 10장 시나리오",
            enabled = false,
            onClick = {}
        )
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "스프레드 선택") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "원하는 스프레드를 골라주세요.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            spreads.forEach { entry ->
                SpreadMenuCard(entry = entry)
            }
        }
    }
}

@Composable
private fun SpreadMenuCard(entry: SpreadMenuEntry) {
    val shape = RoundedCornerShape(24.dp)
    val containerColor = if (entry.enabled) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
    }
    val textColor = if (entry.enabled) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (entry.enabled) {
                    it.clickable(onClick = entry.onClick)
                } else {
                    it
                }
            },
        shape = shape,
        tonalElevation = if (entry.enabled) 4.dp else 0.dp,
        color = containerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = entry.description,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpreadMenuPreview() {
    TarotcardTheme {
        SpreadMenuScreen(onPastPresentFuture = {}, onBack = {})
    }
}

private data class SpreadMenuEntry(
    val title: String,
    val description: String,
    val enabled: Boolean,
    val onClick: () -> Unit
)
