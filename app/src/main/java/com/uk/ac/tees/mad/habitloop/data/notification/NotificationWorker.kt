package com.uk.ac.tees.mad.habitloop.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uk.ac.tees.mad.habitloop.R

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val habitName = inputData.getString(HABIT_NAME_KEY) ?: return Result.failure()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_fire) // Ensure this drawable exists
            .setContentTitle("Habit Reminder")
            .setContentText("Don't forget to complete your habit: $habitName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(habitName.hashCode(), notification)

        return Result.success()
    }

    companion object {
        const val HABIT_NAME_KEY = "HABIT_NAME"
        const val CHANNEL_ID = "HABIT_REMINDER_CHANNEL"
    }
}
