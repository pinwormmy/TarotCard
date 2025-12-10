package com.pinwormmy.midoritarot.ui.state

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pinwormmy.midoritarot.data.SettingsRepository
import com.pinwormmy.midoritarot.ui.theme.TarotSkin
import com.pinwormmy.midoritarot.ui.theme.TarotSkins
import java.time.LocalTime
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LocalizedName(
    val ko: String,
    val en: String,
    val ja: String? = null,
    val th: String? = null
) {
    fun resolve(locale: Locale = Locale.getDefault()): String {
        return when (locale.language.lowercase()) {
            "en" -> en.ifBlank { ko }
            "ja" -> ja?.takeIf { it.isNotBlank() } ?: ko
            "th" -> th?.takeIf { it.isNotBlank() } ?: ko
            else -> ko
        }
    }
}

enum class CardBackStyle(
    val displayName: LocalizedName,
    val assetName: String
) {
    Byzantine(
        displayName = LocalizedName(ko = "비잔틴", en = "Byzantine"),
        assetName = "byzantine"
    ),
    LightBrown(
        displayName = LocalizedName(ko = "라이트 브라운", en = "Light Brown"),
        assetName = "lightbrown"
    ),
    RoseMoon(
        displayName = LocalizedName(ko = "로즈 문", en = "Rose Moon"),
        assetName = "rosemoon"
    ),
    Persia(
        displayName = LocalizedName(ko = "페르시아", en = "Persia"),
        assetName = "persia"
    );

    fun label(locale: Locale = Locale.getDefault()): String = displayName.resolve(locale)
}

enum class CardFaceSkin(
    val displayName: LocalizedName,
    val folder: String,
    val previewImage: String
) {
    Animation(
        displayName = LocalizedName(ko = "일본 애니", en = "Anime"),
        folder = "animation",
        previewImage = "tarot00"
    );

    fun label(locale: Locale = Locale.getDefault()): String = displayName.resolve(locale)
}

enum class AppLanguage(val code: String) {
    System("system"),
    Korean("ko"),
    English("en"),
    Japanese("ja"),
    Thai("th");

    fun toLocaleOrNull(): Locale? = when (this) {
        System -> null
        else -> Locale(code)
    }

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: System
    }
}

@SuppressLint("NewApi")
data class SettingsUiState(
    val skinId: String = TarotSkins.default.id,
    val cardBackStyle: CardBackStyle = CardBackStyle.Byzantine,
    val cardFaceSkin: CardFaceSkin = CardFaceSkin.Animation,
    val dailyCardNotification: Boolean = false,
    val dailyCardTime: LocalTime = LocalTime.of(9, 0),
    val hapticsEnabled: Boolean = true,
    val language: AppLanguage = AppLanguage.System
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

    fun selectLanguage(language: AppLanguage) = persist { it.copy(language = language) }

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
