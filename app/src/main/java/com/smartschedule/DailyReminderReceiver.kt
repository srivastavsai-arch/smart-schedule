package com.smartschedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showDailyReminderNotification(context)
    }

    private fun showDailyReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            SmartScheduleApp.NOTIFICATION_CHANNEL_DAILY
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Smart Schedule")
            .setContentText("Check your tasks for today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                DAILY_REMINDER_ID,
                notification
            )
        } catch (_: SecurityException) {
        }
    }

    companion object {
        private const val DAILY_REMINDER_ID = 1001
    }
}
