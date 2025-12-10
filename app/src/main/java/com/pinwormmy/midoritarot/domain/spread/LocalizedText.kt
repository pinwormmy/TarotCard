package com.pinwormmy.midoritarot.domain.spread

import java.util.Locale

data class LocalizedText(
    val ko: String,
    val en: String,
    val ja: String? = null,
    val th: String? = null
) {
    fun resolve(): String = resolve(Locale.getDefault())

    fun resolve(locale: Locale): String {
        val lang = locale.language.lowercase()
        return when (lang) {
            "en" -> en.ifBlank { ko }
            "ja" -> ja?.takeIf { it.isNotBlank() } ?: ko
            "th" -> th?.takeIf { it.isNotBlank() } ?: ko
            else -> ko
        }
    }
}
