package com.pinwormmy.tarotcard.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.ui.state.CardBackStyle
import com.pinwormmy.tarotcard.ui.state.CardFaceSkin
import com.pinwormmy.tarotcard.ui.state.SettingsUiState
import com.pinwormmy.tarotcard.ui.theme.TarotSkin
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.annotation.SuppressLint

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi") // java.time.* is desugared; safe on minSdk 24
@Composable
fun OptionsScreen(
    settings: SettingsUiState,
    availableSkins: List<TarotSkin>,
    onSelectSkin: (String) -> Unit,
    onSelectCardBack: (CardBackStyle) -> Unit,
    onSelectCardFace: (CardFaceSkin) -> Unit,
    onToggleDailyCard: (Boolean) -> Unit,
    onDailyCardTimeChange: (LocalTime) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val timeFormatter = remember { DateTimeFormatter.ofPattern("a hh:mm") }

    val timePickerDialog = remember(settings.dailyCardTime) {
        TimePickerDialog(
            context,
            { _, hour, minute -> onDailyCardTimeChange(LocalTime.of(hour, minute)) },
            settings.dailyCardTime.hour,
            settings.dailyCardTime.minute,
            false
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "옵션") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OptionSection(title = "배경 선택") {
                SkinSelector(
                    skins = availableSkins,
                    selectedId = settings.skinId,
                    onSelect = onSelectSkin
                )
            }

            OptionSection(title = "카드 뒷면") {
                StyleChips(
                    items = CardBackStyle.values().toList(),
                    selected = settings.cardBackStyle,
                    label = { it.displayName },
                    onSelect = onSelectCardBack
                )
            }

            OptionSection(title = "카드 스킨 선택") {
                StyleChips(
                    items = CardFaceSkin.values().toList(),
                    selected = settings.cardFaceSkin,
                    label = { it.displayName },
                    onSelect = onSelectCardFace
                )
            }

            OptionSection(title = "오늘의 카드 알림") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "알림 받기", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "원하는 시간에 오늘의 카드를 알려드려요.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = settings.dailyCardNotification,
                        onCheckedChange = onToggleDailyCard
                    )
                }
                Text(
                    text = "알림에는 카드 이름과 정방향 의미가 간단한 푸시 메시지로 표시됩니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (settings.dailyCardNotification) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { timePickerDialog.show() },
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            text = settings.dailyCardTime.format(timeFormatter),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            OptionSection(title = "미세 진동") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "카드 인터랙션에 진동 사용", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "드로우/탭 시 촉감을 더해요.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = settings.hapticsEnabled,
                        onCheckedChange = onToggleHaptics
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun OptionSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
private fun SkinSelector(
    skins: List<TarotSkin>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(skins, key = { it.id }) { skin ->
            val isSelected = skin.id == selectedId
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(72.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable { onSelect(skin.id) },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(skin.backgroundBrush)
                            .fillMaxSize()
                    )
                }
                Text(text = skin.displayName, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun <T> StyleChips(
    items: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items) { item ->
            val isSelected = item == selected
            Surface(
                modifier = Modifier.clickable { onSelect(item) },
                shape = CircleShape,
                tonalElevation = if (isSelected) 6.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = label(item),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
