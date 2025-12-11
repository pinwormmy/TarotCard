package com.pinwormmy.midoritarot.data

import android.content.Context
import android.annotation.SuppressLint
import androidx.core.content.edit
import com.pinwormmy.midoritarot.notifications.DailyCardNotificationPreferences
import com.pinwormmy.midoritarot.ui.state.CardBackStyle
import com.pinwormmy.midoritarot.ui.state.CardFaceSkin
import com.pinwormmy.midoritarot.ui.state.SettingsUiState
import com.pinwormmy.midoritarot.ui.state.AppLanguage
import java.time.LocalTime

private const val PREF_NAME = "tarot_settings"
private const val KEY_SKIN_ID = "skin_id"
private const val KEY_CARD_BACK = "card_back"
private const val KEY_CARD_FACE = "card_face"
private const val KEY_DAILY_ENABLED = "daily_enabled"
private const val KEY_DAILY_TIME = "daily_time"
private const val KEY_HAPTICS = "haptics"
private const val KEY_USE_REVERSED = "use_reversed"
private const val KEY_LANGUAGE = "language"

class SettingsRepository(
    private val context: Context
) {
    private val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    @SuppressLint("NewApi")
    fun load(): SettingsUiState {
        val defaults = SettingsUiState()
        val skinId = prefs.getString(KEY_SKIN_ID, defaults.skinId) ?: defaults.skinId
        val cardBack = prefs.getString(KEY_CARD_BACK, null)?.let { name ->
            CardBackStyle.entries.firstOrNull { it.name == name }
        } ?: defaults.cardBackStyle
        val cardFace = prefs.getString(KEY_CARD_FACE, null)?.let { name ->
            CardFaceSkin.entries.firstOrNull { it.name == name }
        } ?: defaults.cardFaceSkin

        val dailyEnabled = prefs.getBoolean(
            KEY_DAILY_ENABLED,
            DailyCardNotificationPreferences.isEnabled(context)
        )
        val dailyTime = prefs.getString(KEY_DAILY_TIME, null)
            ?.let { value -> runCatching { LocalTime.parse(value) }.getOrNull() }
            ?: DailyCardNotificationPreferences.getTime(context)
            ?: defaults.dailyCardTime

        val haptics = prefs.getBoolean(KEY_HAPTICS, defaults.hapticsEnabled)
        val useReversed = prefs.getBoolean(KEY_USE_REVERSED, defaults.useReversedCards)
        val language = prefs.getString(KEY_LANGUAGE, defaults.language.code)?.let { AppLanguage.fromCode(it) }
            ?: defaults.language

        return defaults.copy(
            skinId = skinId,
            cardBackStyle = cardBack,
            cardFaceSkin = cardFace,
            dailyCardNotification = dailyEnabled,
            dailyCardTime = dailyTime,
            hapticsEnabled = haptics,
            useReversedCards = useReversed,
            language = language
        )
    }

    fun save(state: SettingsUiState) {
        prefs.edit {
            putString(KEY_SKIN_ID, state.skinId)
            putString(KEY_CARD_BACK, state.cardBackStyle.name)
            putString(KEY_CARD_FACE, state.cardFaceSkin.name)
            putBoolean(KEY_DAILY_ENABLED, state.dailyCardNotification)
            putString(KEY_DAILY_TIME, state.dailyCardTime.toString())
            putBoolean(KEY_HAPTICS, state.hapticsEnabled)
            putBoolean(KEY_USE_REVERSED, state.useReversedCards)
            putString(KEY_LANGUAGE, state.language.code)
        }
    }
}
