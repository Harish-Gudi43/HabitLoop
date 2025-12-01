package com.uk.ac.tees.mad.habitloop.domain

import com.uk.ac.tees.mad.habitloop.domain.models.Habit
import kotlinx.coroutines.flow.Flow

interface HabitLoopRepository {
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    fun getHabits(): Flow<List<Habit>>
    suspend fun syncWithFirebase()
    suspend fun getHabitStats(): Map<String, Int>
    suspend fun backupHabits()
    suspend fun restoreHabits()
    suspend fun clearHabits()
}
