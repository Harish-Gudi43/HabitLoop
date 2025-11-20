package com.uk.ac.tees.mad.habitloop.data.notification

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleNotification(habitName: String, reminderTime: Long) {
        val workManager = WorkManager.getInstance(context)

        val data = workDataOf(NotificationWorker.HABIT_NAME_KEY to habitName)

        val delay = reminderTime - System.currentTimeMillis()

        if (delay > 0) {
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            workManager.enqueue(workRequest)
        }
    }
}
