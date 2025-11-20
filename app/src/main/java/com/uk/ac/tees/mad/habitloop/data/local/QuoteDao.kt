package com.uk.ac.tees.mad.habitloop.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Query("SELECT * FROM quotes ORDER BY id DESC LIMIT 1")
    fun getLatestQuote(): Flow<QuoteEntity?>
}
