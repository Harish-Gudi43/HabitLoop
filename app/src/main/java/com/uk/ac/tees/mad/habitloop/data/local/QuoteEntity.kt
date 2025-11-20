package com.uk.ac.tees.mad.habitloop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val author: String
) {
    fun toDomain() = com.uk.ac.tees.mad.habitloop.domain.models.Quote(text, author)
}
