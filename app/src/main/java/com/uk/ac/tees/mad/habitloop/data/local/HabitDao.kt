package com.uk.ac.tees.mad.habitloop.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Upsert
    suspend fun upsertHabit(habit: HabitEntity)

    @Upsert
    suspend fun insertAll(habits: List<HabitEntity>)

    @Query("SELECT * FROM habits")
    fun getHabits(): Flow<List<HabitEntity>>

    @Query("DELETE FROM habits")
    suspend fun clear()
}
