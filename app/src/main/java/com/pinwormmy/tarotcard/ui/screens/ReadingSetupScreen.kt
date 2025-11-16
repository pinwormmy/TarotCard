package com.pinwormmy.tarotcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinwormmy.tarotcard.ui.state.SpreadPosition

@Composable
fun ReadingSetupScreen(
    positions: List<SpreadPosition>,
    questionText: String,
    useReversedCards: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onQuestionChange: (String) -> Unit,
    onUseReversedChange: (Boolean) -> Unit,
    onShuffle: () -> Unit,
    onQuickReading: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "과거 / 현재 / 미래", fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = onBack) {
                    Text(text = "뒤로")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                positions.forEach { position ->
                    ReadingSetupCard(
                        modifier = Modifier.weight(1f),
                        label = position.title
                    )
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = questionText,
                onValueChange = onQuestionChange,
                placeholder = { Text(text = "질문을 입력하세요(선택사항)") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "리버스 카드 사용하기", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "카드 의미를 정방향과 역방향 모두 해석합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = useReversedCards,
                    onCheckedChange = onUseReversedChange
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onShuffle
            ) {
                Text(text = "카드 셔플")
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
private fun ReadingSetupCard(
    label: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3C3D63), Color(0xFF1A1B30))
                ),
                shape = shape
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "셔플에서 카드가 결정됩니다",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}
