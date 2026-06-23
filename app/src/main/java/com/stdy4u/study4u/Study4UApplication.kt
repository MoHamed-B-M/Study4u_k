package com.stdy4u.study4u

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Study4UApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)
        val channels = listOf(
            NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            NotificationChannel(
                CHANNEL_CLASS_REMINDERS,
                "Class Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ),
            NotificationChannel(
                CHANNEL_APP_UPDATES,
                "App Updates",
                NotificationManager.IMPORTANCE_LOW
            ),
            NotificationChannel(
                CHANNEL_FOCUS_TIMER,
                "Focus Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
            }
        )
        channels.forEach { manager.createNotificationChannel(it) }
    }

    companion object {
        const val CHANNEL_GENERAL = "general"
        const val CHANNEL_CLASS_REMINDERS = "class_reminders"
        const val CHANNEL_APP_UPDATES = "app_updates"
        const val CHANNEL_FOCUS_TIMER = "focus_timer"
    }
}
