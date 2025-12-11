package com.pinwormmy.midoritarot.core.localization

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

/**
 * Simple locale-aware string holder for ko/en/ja/th with graceful fallback.
 */
data class LocalizedString(
    val ko: String,
    val en: String? = null,
    val ja: String? = null,
    val th: String? = null
) {
    fun resolve(
        locale: Locale = AppCompatDelegate.getApplicationLocales().get(0) ?: Locale.getDefault()
    ): String {
        val lang = locale.language.lowercase()
        return when (lang) {
            "en" -> en?.takeIf { it.isNotBlank() } ?: ko
            "ja" -> ja?.takeIf { it.isNotBlank() } ?: en ?: ko
            "th" -> th?.takeIf { it.isNotBlank() } ?: en ?: ko
            "ko" -> ko.ifBlank { en ?: ja ?: th ?: ko }
            else -> en ?: ja ?: th ?: ko
        }
    }
}
