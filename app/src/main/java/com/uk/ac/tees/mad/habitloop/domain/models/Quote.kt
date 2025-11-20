package com.uk.ac.tees.mad.habitloop.domain.models

import com.uk.ac.tees.mad.habitloop.data.local.QuoteEntity
import kotlinx.serialization.Serializable

// Domain model
data class Quote(
    val text: String,
    val author: String
)

// DTO for zenquotes.io API response
@Serializable
data class QuoteDto(
    val q: String,
    val a: String
) {
    fun toDomain() = Quote(text = q, author = a)
}

// Mapper for entity
fun Quote.toEntity() = QuoteEntity(text = text, author = author)
