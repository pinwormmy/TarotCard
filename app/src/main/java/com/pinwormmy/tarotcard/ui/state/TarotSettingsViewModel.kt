package com.pinwormmy.tarotcard.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import com.pinwormmy.tarotcard.ui.theme.TarotSkin
import com.pinwormmy.tarotcard.ui.theme.TarotSkins

enum class CardBackStyle(
    val displayName: String,
    val assetName: String
) {
    Byzantine("비잔틴", "byzantine"),
    LightBrown("라이트 브라운", "lightbrown"),
    RoseMoon("로즈 문", "rosemoon"),
    Persia("페르시아", "persia")
}

enum class CardFaceSkin(
    val displayName: String,
    val folder: String,
    val previewImage: String
) {
    Animation("애니메이션", "animation", "tarot00")
}

data class SettingsUiState(
    val skinId: String = TarotSkins.default.id,
    val cardBackStyle: CardBackStyle = CardBackStyle.Byzantine,
    val cardFaceSkin: CardFaceSkin = CardFaceSkin.Animation,
    val dailyCardNotification: Boolean = false,
    val dailyCardTime: LocalTime = LocalTime.of(9, 0),
    val hapticsEnabled: Boolean = true
) {
    val skin: TarotSkin = TarotSkins.findById(skinId)
}

class TarotSettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun selectSkin(id: String) {
        _uiState.update { it.copy(skinId = id) }
    }

    fun selectCardBack(style: CardBackStyle) {
        _uiState.update { it.copy(cardBackStyle = style) }
    }

    fun selectCardFace(style: CardFaceSkin) {
        _uiState.update { it.copy(cardFaceSkin = style) }
    }

    fun toggleDailyCard(enabled: Boolean) {
        _uiState.update { it.copy(dailyCardNotification = enabled) }
    }

    fun updateDailyCardTime(time: LocalTime) {
        _uiState.update { it.copy(dailyCardTime = time) }
    }

    fun toggleHaptics(enabled: Boolean) {
        _uiState.update { it.copy(hapticsEnabled = enabled) }
    }
}
