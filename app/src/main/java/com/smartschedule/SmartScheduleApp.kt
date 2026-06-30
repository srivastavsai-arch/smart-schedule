package com.smartschedule

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.smartschedule.worker.DailyMaintenanceWorker

class SmartScheduleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        DailyMaintenanceWorker.schedule(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val taskChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_TASKS,
                "Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for your scheduled tasks"
                enableVibration(true)
            }

            val dailyChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_DAILY,
                "Daily Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily task reminder notification"
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(taskChannel)
            notificationManager.createNotificationChannel(dailyChannel)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_TASKS = "task_reminders"
        const val NOTIFICATION_CHANNEL_DAILY = "daily_reminder"
    }
}
