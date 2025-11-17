package com.pinwormmy.tarotcard.notifications

import android.content.Context
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

object DailyCardNotificationScheduler {
    private const val WORK_NAME = "daily_card_notification"
    internal const val KEY_SCHEDULED_TIME = "scheduled_time"

    fun schedule(context: Context, time: LocalTime) {
        DailyCardNotificationPreferences.setEnabled(context, true)
        DailyCardNotificationPreferences.setTime(context, time)
        val delayMillis = computeDelayMillis(time)
        val request = OneTimeWorkRequestBuilder<DailyCardNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(KEY_SCHEDULED_TIME to time.toString()))
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context) {
        DailyCardNotificationPreferences.setEnabled(context, false)
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    internal fun rescheduleFromWorker(context: Context, time: LocalTime) {
        if (DailyCardNotificationPreferences.isEnabled(context)) {
            schedule(context, time)
        }
    }

    private fun computeDelayMillis(time: LocalTime): Long {
        val now = ZonedDateTime.now()
        var trigger = now.withHour(time.hour).withMinute(time.minute).withSecond(0).withNano(0)
        if (!trigger.isAfter(now)) {
            trigger = trigger.plusDays(1)
        }
        return Duration.between(now, trigger).toMillis()
    }
}
