package com.uk.ac.tees.mad.habitloop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [HabitEntity::class, QuoteEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HabitLoopDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun quoteDao(): QuoteDao
}
