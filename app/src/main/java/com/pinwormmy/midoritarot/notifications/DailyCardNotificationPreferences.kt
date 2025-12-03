package com.pinwormmy.midoritarot.notifications

import android.content.Context
import java.time.LocalTime

private const val PREF_NAME = "daily_card_notification"
private const val KEY_ENABLED = "enabled"
private const val KEY_TIME = "time"

object DailyCardNotificationPreferences {
    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)

    fun setTime(context: Context, time: LocalTime) {
        prefs(context).edit().putString(KEY_TIME, time.toString()).apply()
    }

    fun getTime(context: Context): LocalTime? =
        prefs(context).getString(KEY_TIME, null)?.let { LocalTime.parse(it) }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
}
