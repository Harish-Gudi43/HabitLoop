package com.uk.ac.tees.mad.habitloop.domain

import com.uk.ac.tees.mad.habitloop.domain.models.Quote
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    fun getQuote(): Flow<Quote?>
    suspend fun refreshQuote()
}
