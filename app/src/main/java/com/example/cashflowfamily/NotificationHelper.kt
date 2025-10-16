package com.example.cashflowfamily

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val BUDGET_CHANNEL_ID = "BUDGET_ALERT_CHANNEL"
    private const val BUDGET_NOTIFICATION_ID = 101

    fun showBudgetAlertNotification(context: Context, category: String, percentage: Int) {
        createBudgetNotificationChannel(context)

        val message = "Pengeluaran untuk kategori '$category' sudah mencapai $percentage% dari anggaran."

        val builder = NotificationCompat.Builder(context, BUDGET_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle("Peringatan Batas Anggaran")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(BUDGET_NOTIFICATION_ID, builder.build())
            }
        } else {
            NotificationManagerCompat.from(context).notify(BUDGET_NOTIFICATION_ID, builder.build())
        }
    }

    private fun createBudgetNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Peringatan Anggaran"
            val descriptionText = "Notifikasi saat pengeluaran mendekati batas anggaran"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(BUDGET_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}