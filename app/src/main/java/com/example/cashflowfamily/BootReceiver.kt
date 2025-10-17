package com.example.cashflowfamily

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPref = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
            val isReminderEnabled = sharedPref.getBoolean("is_enabled", false)

            if (isReminderEnabled) {
                val hour = sharedPref.getInt("hour", 20)
                val minute = sharedPref.getInt("minute", 0)

                NotificationScheduler.setReminder(context, hour, minute)
            }
        }
    }
}