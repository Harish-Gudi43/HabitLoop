package com.uk.ac.tees.mad.habitloop.data.remote

import com.uk.ac.tees.mad.habitloop.domain.models.QuoteDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class QuoteApi(private val client: HttpClient) {
    suspend fun getRandomQuote(): List<QuoteDto> {
        return client.get("https://zenquotes.io/api/random").body()
    }
}
