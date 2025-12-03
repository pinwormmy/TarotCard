package com.pinwormmy.midoritarot.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pinwormmy.midoritarot.MainActivity
import com.pinwormmy.midoritarot.R
import java.time.LocalTime

class DailyCardNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (!DailyCardNotificationPreferences.isEnabled(applicationContext)) {
            return Result.success()
        }
        val scheduledTime = inputData.getString(DailyCardNotificationScheduler.KEY_SCHEDULED_TIME)
            ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
            ?: DailyCardNotificationPreferences.getTime(applicationContext)
            ?: LocalTime.of(9, 0)

        showNotification()
        DailyCardNotificationScheduler.rescheduleFromWorker(applicationContext, scheduledTime)
        return Result.success()
    }

    private fun showNotification() {
        val channelId = "daily_card_channel"
        ensureChannel(channelId)
        if (!canPostNotifications(channelId)) return

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("타로테스트")
            .setContentText("오늘은 무슨 일이 생길까? 오늘의 타로카드를 뽑아보세요!")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "오늘은 무슨 일이 생길까? 오늘의 타로카드를 뽑아보세요!"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
    }

    private fun canPostNotifications(channelId: String): Boolean {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        if (!hasPermission) return false
        if (!notificationManager.areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            if (channel?.importance == NotificationManager.IMPORTANCE_NONE) return false
        }
        return true
    }

    private fun ensureChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                applicationContext.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "오늘의 카드 알림"
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
