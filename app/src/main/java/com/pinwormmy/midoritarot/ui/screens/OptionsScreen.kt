package com.pinwormmy.midoritarot.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.components.CARD_ASPECT_RATIO
import com.pinwormmy.midoritarot.ui.components.CardBackArt
import com.pinwormmy.midoritarot.ui.components.CardFaceArt
import com.pinwormmy.midoritarot.ui.components.TarotCardShape
import com.pinwormmy.midoritarot.ui.state.CardBackStyle
import com.pinwormmy.midoritarot.ui.state.CardFaceSkin
import com.pinwormmy.midoritarot.ui.state.SettingsUiState
import com.pinwormmy.midoritarot.ui.theme.TarotSkin
import com.pinwormmy.midoritarot.ui.theme.HapticsPlayer
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
    val hapticFeedback = LocalHapticFeedback.current
    val timeFormatter = remember { DateTimeFormatter.ofPattern("a hh:mm") }
    val containerBounds = remember { mutableStateOf<Rect?>(null) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onToggleDailyCard(granted)
    }

    val timePickerDialog = remember(settings.dailyCardTime) {
        TimePickerDialog(
            context,
            { _, hour, minute -> onDailyCardTimeChange(LocalTime.of(hour, minute)) },
            settings.dailyCardTime.hour,
            settings.dailyCardTime.minute,
            false
        )
    }
    var previewSkin by remember { mutableStateOf<Pair<CardFaceSkin, Rect?>?>(null) }
    var previewBack by remember { mutableStateOf<Pair<CardBackStyle, Rect?>?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = "옵션") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .onGloballyPositioned { containerBounds.value = it.boundsInRoot() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                OptionSection(title = "카드 뒷면 선택") {
                    CardBackSkinSelector(
                        backs = CardBackStyle.entries.toList(),
                        selected = settings.cardBackStyle,
                        onPreview = { back, bounds -> previewBack = back to bounds }
                    )
                }

                OptionSection(title = "카드 스킨 선택") {
                    CardFaceSkinSelector(
                        skins = CardFaceSkin.entries.toList(),
                        selected = settings.cardFaceSkin,
                        onPreview = { skin, bounds -> previewSkin = skin to bounds }
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
                                text = "원하는 시간에 오늘의 카드를 뽑도록 알려드려요.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = settings.dailyCardNotification,
                            onCheckedChange = { enabled ->
                                if (!enabled) {
                                    onToggleDailyCard(false)
                                    return@Switch
                                }
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                    onToggleDailyCard(true)
                                    return@Switch
                                }
                                val permissionGranted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                                if (permissionGranted) {
                                    onToggleDailyCard(true)
                                } else {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        )
                    }
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
                            onCheckedChange = { enabled ->
                                onToggleHaptics(enabled)
                                if (enabled) {
                                    HapticsPlayer.tripleConfirm(context, hapticFeedback)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            previewSkin?.let { (skin, bounds) ->
                CardFacePreviewModal(
                    skin = skin,
                    isSelected = skin == settings.cardFaceSkin,
                    containerBounds = containerBounds.value,
                    startBounds = bounds,
                    onSelect = {
                        onSelectCardFace(skin)
                        previewSkin = null
                    },
                    onDismiss = { previewSkin = null }
                )
            }
            previewBack?.let { (back, bounds) ->
                CardBackPreviewModal(
                    back = back,
                    isSelected = back == settings.cardBackStyle,
                    containerBounds = containerBounds.value,
                    startBounds = bounds,
                    onSelect = {
                        onSelectCardBack(back)
                        previewBack = null
                    },
                    onDismiss = { previewBack = null }
                )
            }
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
private fun CardFaceSkinSelector(
    skins: List<CardFaceSkin>,
    selected: CardFaceSkin,
    onPreview: (CardFaceSkin, Rect?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(skins) { skin ->
            val isSelected = skin == selected
            val previewCard = remember(skin) { previewCardForSkin(skin) }
            val itemBounds = remember { mutableStateOf<Rect?>(null) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .width(96.dp)
                        .aspectRatio(CARD_ASPECT_RATIO)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(
                                alpha = 0.3f
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .onGloballyPositioned { itemBounds.value = it.boundsInRoot() }
                        .clickable { onPreview(skin, itemBounds.value) },
                    shape = RoundedCornerShape(18.dp),
                    tonalElevation = if (isSelected) 6.dp else 2.dp
                ) {
                    CardFaceArt(
                        card = previewCard,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    text = skin.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun CardBackSkinSelector(
    backs: List<CardBackStyle>,
    selected: CardBackStyle,
    onPreview: (CardBackStyle, Rect?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(backs) { back ->
            val isSelected = back == selected
            val itemBounds = remember { mutableStateOf<Rect?>(null) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .width(96.dp)
                        .aspectRatio(CARD_ASPECT_RATIO)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(
                                alpha = 0.3f
                            ),
                            shape = TarotCardShape
                        )
                        .onGloballyPositioned { itemBounds.value = it.boundsInRoot() }
                        .clickable { onPreview(back, itemBounds.value) },
                    shape = TarotCardShape,
                    tonalElevation = if (isSelected) 6.dp else 2.dp
                ) {
                    CardBackArt(
                        modifier = Modifier.fillMaxSize(),
                        backStyle = back,
                        shape = TarotCardShape
                    )
                }
                Text(
                    text = back.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun CardFacePreviewModal(
    skin: CardFaceSkin,
    isSelected: Boolean,
    containerBounds: Rect?,
    startBounds: Rect?,
    onSelect: () -> Unit,
    onDismiss: () -> Unit
) {
    val previewCard = remember(skin) { previewCardForSkin(skin) }
    val progress = remember { Animatable(0f) }
    val density = LocalDensity.current
    BackHandler(onBack = onDismiss)

    LaunchedEffect(skin) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
        )
    }

    val container = containerBounds
    val targetWidthPx = container?.let {
        val maxWidth = it.width * 0.72f
        val maxHeight = it.height * 0.82f
        val widthFromHeight = maxHeight * CARD_ASPECT_RATIO
        minOf(maxWidth, widthFromHeight)
    } ?: with(density) { 220.dp.toPx() }
    val targetWidthDp = with(density) { targetWidthPx.toDp() }

    val containerCenter = container?.center ?: Offset.Zero
    val startCenter = startBounds?.center ?: containerCenter
    val startScale = startBounds?.width?.div(targetWidthPx)?.coerceAtLeast(0.5f) ?: 0.85f
    val startOffset = startCenter - containerCenter
    val eased = FastOutSlowInEasing.transform(progress.value.coerceIn(0f, 1f))
    val overlayAlpha = eased * 0.6f
    val scale = startScale + (1f - startScale) * eased
    val offsetX = startOffset.x * (1f - eased)
    val offsetY = startOffset.y * (1f - eased)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                    alpha = 0.6f + 0.4f * eased
                }
                .background(Color(0xFF1F1F2E), RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = skin.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(18.dp),
                tonalElevation = 8.dp
            ) {
                CardFaceArt(
                    card = previewCard,
                    modifier = Modifier
                        .width(targetWidthDp)
                        .aspectRatio(CARD_ASPECT_RATIO)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onSelect
                ) {
                    Text(text = if (isSelected) "선택됨" else "스킨 선택")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                ) {
                    Text(text = "취소")
                }
            }
        }
    }
}

@Composable
private fun CardBackPreviewModal(
    back: CardBackStyle,
    isSelected: Boolean,
    containerBounds: Rect?,
    startBounds: Rect?,
    onSelect: () -> Unit,
    onDismiss: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val density = LocalDensity.current
    BackHandler(onBack = onDismiss)

    LaunchedEffect(back) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
        )
    }

    val container = containerBounds
    val targetWidthPx = container?.let {
        val maxWidth = it.width * 0.72f
        val maxHeight = it.height * 0.82f
        val widthFromHeight = maxHeight * CARD_ASPECT_RATIO
        minOf(maxWidth, widthFromHeight)
    } ?: with(density) { 220.dp.toPx() }
    val targetWidthDp = with(density) { targetWidthPx.toDp() }

    val containerCenter = container?.center ?: Offset.Zero
    val startCenter = startBounds?.center ?: containerCenter
    val startScale = startBounds?.width?.div(targetWidthPx)?.coerceAtLeast(0.5f) ?: 0.85f
    val startOffset = startCenter - containerCenter
    val eased = FastOutSlowInEasing.transform(progress.value.coerceIn(0f, 1f))
    val overlayAlpha = eased * 0.6f
    val scale = startScale + (1f - startScale) * eased
    val offsetX = startOffset.x * (1f - eased)
    val offsetY = startOffset.y * (1f - eased)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                    alpha = 0.6f + 0.4f * eased
                }
                .background(Color(0xFF1F1F2E), RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = back.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(18.dp),
                tonalElevation = 8.dp
            ) {
                CardBackArt(
                    modifier = Modifier
                        .width(targetWidthDp)
                        .aspectRatio(CARD_ASPECT_RATIO),
                    backStyle = back,
                    shape = TarotCardShape
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onSelect
                ) {
                    Text(text = if (isSelected) "선택됨" else "뒷면 선택")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                ) {
                    Text(text = "취소")
                }
            }
        }
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
                    ) {
                        skin.backgroundImageRes?.let { resId ->
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alpha = 0.8f
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(skin.backgroundOverlayColor)
                        )
                    }
                }
                Text(text = skin.displayName, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun previewCardForSkin(skin: CardFaceSkin): TarotCardModel {
    return TarotCardModel(
        id = "preview_${skin.previewImage}",
        name = "Preview",
        arcana = "Preview",
        uprightMeaning = "",
        reversedMeaning = "",
        description = "",
        keywords = emptyList(),
        imageUrl = skin.previewImage
    )
}
