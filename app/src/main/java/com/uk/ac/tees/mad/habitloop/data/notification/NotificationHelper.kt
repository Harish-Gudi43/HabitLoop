package com.uk.ac.tees.mad.habitloop.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.uk.ac.tees.mad.habitloop.R

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showQuoteNotification(quote: String) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, QUOTE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Your Daily Quote")
            .setContentText(quote)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(QUOTE_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Quotes"
            val descriptionText = "Channel for daily motivational quotes"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(QUOTE_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val QUOTE_CHANNEL_ID = "quote_channel"
        private const val QUOTE_NOTIFICATION_ID = 1
    }
}
