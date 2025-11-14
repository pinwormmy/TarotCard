package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReadingSetupScreen(
    question: String,
    useReversed: Boolean,
    selectedCount: Int,
    modifier: Modifier = Modifier,
    canImmediateReading: Boolean,
    onQuestionChange: (String) -> Unit,
    onToggleReversed: () -> Unit,
    onShuffleRequested: () -> Unit,
    onImmediateReading: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "리딩 준비", fontWeight = FontWeight.Bold)
            Text(text = "선택한 카드 수: $selectedCount / 3")
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = question,
                onValueChange = onQuestionChange,
                placeholder = { Text(text = "질문을 입력하세요 (선택사항)") }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = useReversed,
                    onCheckedChange = { onToggleReversed() }
                )
                Text(text = "리버스 카드 사용하기")
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onShuffleRequested
            ) {
                Text(text = "카드 셔플")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onImmediateReading,
                enabled = canImmediateReading
            ) {
                Text(text = "바로 리딩하기")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBack
            ) {
                Text(text = "이전 단계")
            }
        }
    }
}
