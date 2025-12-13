package com.pinwormmy.midoritarot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.res.stringResource
import com.pinwormmy.midoritarot.ui.components.SpreadBoard
import com.pinwormmy.midoritarot.ui.components.estimatedBoardHeight
import com.pinwormmy.midoritarot.domain.spread.SpreadCatalog
import com.pinwormmy.midoritarot.domain.spread.SpreadDefinition
import com.pinwormmy.midoritarot.domain.spread.SpreadType
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme
import com.pinwormmy.midoritarot.R

@Composable
fun ReadingSetupScreen(
    spread: SpreadDefinition,
    questionText: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onQuestionChange: (String) -> Unit,
    onShuffle: () -> Unit,
    onQuickReading: () -> Unit
) {
    val isCelticCross = spread.type == SpreadType.CelticCross
    var raiseFirstCard by remember(spread) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = spread.title.resolve(), fontWeight = FontWeight.Bold)
                    Text(
                        text = spread.description.resolve(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                OutlinedButton(onClick = onBack) {
                    Text(text = stringResource(id = R.string.reading_setup_back))
                }
            }

            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = questionText,
                onValueChange = { onQuestionChange(it.replace('\n', ' ').replace('\r', ' ')) },
                placeholder = { Text(text = spread.questionPlaceholder.resolve()) },
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                ),
            )

            SpreadBoard(
                layout = spread.layout,
                positions = spread.positions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spread.estimatedBoardHeight())
                    .padding(top = 8.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(16.dp)
            ) { _, position, cardModifier ->
                val previewModifier = if (isCelticCross && position.order == 1) {
                    cardModifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    raiseFirstCard = true
                                    try {
                                        tryAwaitRelease()
                                    } finally {
                                        raiseFirstCard = false
                                    }
                                }
                            )
                        }
                        .zIndex(if (raiseFirstCard) 10f else position.placement.zIndex)
                } else {
                    cardModifier.zIndex(position.placement.zIndex)
                }
                SpreadPreviewCard(
                    modifier = previewModifier,
                    order = position.order,
                    title = position.title.resolve()
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
                Text(text = stringResource(id = R.string.reading_setup_shuffle))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onQuickReading
            ) {
                Text(text = stringResource(id = R.string.reading_setup_start_now))
            }
        }
    }
}

@Composable
private fun SpreadPreviewCard(
    order: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
        )
    )
    Surface(
        modifier = modifier,
        shape = shape,
        tonalElevation = 8.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = order.toString(), fontWeight = FontWeight.Bold)
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadingSetupPreview() {
    TarotcardTheme {
        ReadingSetupScreen(
            spread = SpreadCatalog.default,
            questionText = "",
            onBack = {},
            onQuestionChange = {},
            onShuffle = {},
            onQuickReading = {}
        )
    }
}
