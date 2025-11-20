package com.uk.ac.tees.mad.habitloop.data

import android.util.Log
import com.uk.ac.tees.mad.habitloop.data.local.QuoteDao
import com.uk.ac.tees.mad.habitloop.data.remote.QuoteApi
import com.uk.ac.tees.mad.habitloop.domain.QuoteRepository
import com.uk.ac.tees.mad.habitloop.domain.models.Quote
import com.uk.ac.tees.mad.habitloop.domain.models.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuoteRepositoryImp(
    private val quoteDao: QuoteDao,
    private val api: QuoteApi
) : QuoteRepository {

    override fun getQuote(): Flow<Quote?> {
        return quoteDao.getLatestQuote().map { it?.toDomain() }
    }

    override suspend fun refreshQuote() {
        try {
            val remoteQuote = api.getRandomQuote().first()
            quoteDao.insertQuote(remoteQuote.toDomain().toEntity())
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Failed to refresh quote", e)
        }
    }
}
