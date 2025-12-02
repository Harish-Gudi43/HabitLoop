package com.uk.ac.tees.mad.habitloop.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uk.ac.tees.mad.habitloop.domain.QuoteRepository
import kotlinx.coroutines.flow.first

class QuoteNotificationWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val quoteRepository: QuoteRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val quoteResult = quoteRepository.getQuote().first()
        if (quoteResult is com.uk.ac.tees.mad.habitloop.domain.util.Result.Success<*>) {
            val quote = (quoteResult.data as? com.uk.ac.tees.mad.habitloop.domain.models.Quote)?.text
            if (quote != null) {
                NotificationHelper(applicationContext).showQuoteNotification(quote)
            }
        }
        return Result.success()
    }

    companion object {
        const val TAG = "QuoteNotificationWorker"
    }
}
