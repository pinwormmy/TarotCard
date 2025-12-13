package com.pinwormmy.midoritarot.core.localization

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

fun currentAppLocale(context: Context? = null): Locale {
    val appLocale = AppCompatDelegate.getApplicationLocales().get(0)
    if (appLocale != null) return appLocale

    if (context != null) {
        val locales = context.resources.configuration.locales
        if (locales.size() > 0) {
            return locales.get(0)
        }
    }

    return Locale.getDefault()
}

