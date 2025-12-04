package com.pinwormmy.midoritarot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme

@Composable
fun MainMenuScreen(
    onStartReading: () -> Unit,
    modifier: Modifier = Modifier,
    onDailyCard: (() -> Unit)? = null,
    onBrowseCards: (() -> Unit)? = null,
    onOpenOptions: (() -> Unit)? = null
) {
    val menuItems = listOf(
        MainMenuEntry(
            label = "오늘의 카드",
            enabled = onDailyCard != null,
            onClick = onDailyCard ?: {}
        ),
        MainMenuEntry(
            label = "리딩 시작",
            enabled = true,
            onClick = onStartReading
        ),
        MainMenuEntry(
            label = "카드 살펴보기",
            enabled = onBrowseCards != null,
            onClick = onBrowseCards ?: {}
        ),
        MainMenuEntry(
            label = "옵션",
            enabled = onOpenOptions != null,
            onClick = onOpenOptions ?: {}
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "타로테스트",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "오늘의 에너지를 비추는 타로카드",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                menuItems.forEach { entry ->
                    MainMenuButton(
                        label = entry.label,
                        enabled = entry.enabled,
                        onClick = entry.onClick
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun MainMenuButton(
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = if (enabled) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
    }
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .let {
                if (enabled) {
                    it.clickable(onClick = onClick)
                } else {
                    it
                }
            },
        color = containerColor,
        tonalElevation = if (enabled) 6.dp else 0.dp
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(vertical = 20.dp, horizontal = 24.dp)),
            text = label,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainMenuPreview() {
    TarotcardTheme {
        MainMenuScreen(onStartReading = {})
    }
}

private data class MainMenuEntry(
    val label: String,
    val enabled: Boolean,
    val onClick: () -> Unit
)
