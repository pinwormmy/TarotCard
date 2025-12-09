package com.pinwormmy.midoritarot.ui.state

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pinwormmy.midoritarot.data.SettingsRepository
import com.pinwormmy.midoritarot.ui.theme.TarotSkin
import com.pinwormmy.midoritarot.ui.theme.TarotSkins
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    Animation("일본 애니", "animation", "tarot00")
}

@SuppressLint("NewApi")
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

class TarotSettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(repository.load())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun selectSkin(id: String) = persist { it.copy(skinId = id) }

    fun selectCardBack(style: CardBackStyle) = persist { it.copy(cardBackStyle = style) }

    fun selectCardFace(style: CardFaceSkin) = persist { it.copy(cardFaceSkin = style) }

    fun toggleDailyCard(enabled: Boolean) = persist { it.copy(dailyCardNotification = enabled) }

    fun updateDailyCardTime(time: LocalTime) = persist { it.copy(dailyCardTime = time) }

    fun toggleHaptics(enabled: Boolean) = persist { it.copy(hapticsEnabled = enabled) }

    private fun persist(reducer: (SettingsUiState) -> SettingsUiState) {
        _uiState.update { current ->
            val updated = reducer(current)
            repository.save(updated)
            updated
        }
    }

    companion object {
        fun factory(repository: SettingsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(TarotSettingsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return TarotSettingsViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
