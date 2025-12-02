package com.uk.ac.tees.mad.habitloop.domain

import com.uk.ac.tees.mad.habitloop.domain.models.Habit
import com.uk.ac.tees.mad.habitloop.domain.util.DataError
import com.uk.ac.tees.mad.habitloop.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface HabitLoopRepository {
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    fun getHabits(): Flow<List<Habit>>
    suspend fun syncWithFirebase()
    suspend fun getHabitStats(): Map<String, Int>
    suspend fun backupHabits(): EmptyResult<DataError.Firebase>
    suspend fun restoreHabits(): EmptyResult<DataError.Firebase>
    suspend fun clearHabits(): EmptyResult<DataError.Firebase>
}
