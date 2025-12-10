package com.pinwormmy.midoritarot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme
import com.pinwormmy.midoritarot.R

@Composable
fun MainMenuScreen(
    onStartReading: () -> Unit,
    modifier: Modifier = Modifier,
    onDailyCard: (() -> Unit)? = null,
    onBrowseCards: (() -> Unit)? = null,
    onOpenOptions: (() -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.smallestScreenWidthDp >= 600

    val menuItems = listOf(
        MainMenuEntry(
            label = stringResource(id = R.string.menu_daily_card),
            enabled = onDailyCard != null,
            onClick = onDailyCard ?: {}
        ),
        MainMenuEntry(
            label = stringResource(id = R.string.menu_start_reading),
            enabled = true,
            onClick = onStartReading
        ),
        MainMenuEntry(
            label = stringResource(id = R.string.menu_browse_cards),
            enabled = onBrowseCards != null,
            onClick = onBrowseCards ?: {}
        ),
        MainMenuEntry(
            label = stringResource(id = R.string.menu_options),
            enabled = onOpenOptions != null,
            onClick = onOpenOptions ?: {}
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 24.dp, vertical = if (isTablet) 24.dp else 32.dp)
    ) {
        val constraintsMaxHeight = maxHeight
        val columnSpacing = if (isTablet) 16.dp else 24.dp
        val buttonSpacing = if (isTablet) 12.dp else 16.dp
        val availableHeight = if (isTablet) constraintsMaxHeight - columnSpacing else Dp.Unspecified
        val menuHeight: Dp = if (isTablet && availableHeight != Dp.Unspecified) {
            availableHeight * 0.4f
        } else {
            Dp.Unspecified
        }
        val logoHeight: Dp = if (isTablet && availableHeight != Dp.Unspecified) {
            availableHeight - menuHeight
        } else {
            Dp.Unspecified
        }
        val menuArrangement = Arrangement.spacedBy(buttonSpacing)
        val availableForButtons = if (isTablet && menuHeight != Dp.Unspecified) {
            menuHeight - buttonSpacing * (menuItems.size - 1)
        } else {
            Dp.Unspecified
        }
        val desiredButtonHeight = 60.dp
        val buttonFixedHeight = if (isTablet && availableForButtons != Dp.Unspecified) {
            desiredButtonHeight.coerceAtMost(availableForButtons / menuItems.size)
        } else {
            desiredButtonHeight
        }
        val buttonPadding = PaddingValues(vertical = if (isTablet) 16.dp else 20.dp, horizontal = 24.dp)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = if (isTablet) Arrangement.spacedBy(columnSpacing) else Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .let { base ->
                        if (logoHeight != Dp.Unspecified) {
                            base.height(logoHeight)
                        } else {
                            base
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.title_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(if (isTablet) 0.73f else 0.81f)
                        .aspectRatio(700f / 674f),
                    contentScale = ContentScale.Fit
                )
            }

            if (!isTablet) {
                Spacer(modifier = Modifier.height(buttonSpacing * 1.25f))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .let { base ->
                        if (menuHeight != Dp.Unspecified) {
                            base.height(menuHeight)
                        } else {
                            base
                        }
                    },
                verticalArrangement = menuArrangement
            ) {
                menuItems.forEach { entry ->
                    MainMenuButton(
                        label = entry.label,
                        enabled = entry.enabled,
                        onClick = entry.onClick,
                        fixedHeight = buttonFixedHeight,
                        contentPadding = buttonPadding
                    )
                }
                if (!isTablet) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun MainMenuButton(
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    fixedHeight: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(vertical = 20.dp, horizontal = 24.dp)
) {
    val containerColor = if (enabled) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)
    }
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = fixedHeight)
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
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
