package com.pinwormmy.tarotcard.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ShuffleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Shuffle",
    enabled: Boolean = true
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = text)
    }
}
