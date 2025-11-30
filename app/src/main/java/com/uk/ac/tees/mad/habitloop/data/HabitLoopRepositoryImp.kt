package com.uk.ac.tees.mad.habitloop.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.habitloop.data.local.HabitDao
import com.uk.ac.tees.mad.habitloop.data.mapper.toDomain
import com.uk.ac.tees.mad.habitloop.data.mapper.toEntity
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import com.uk.ac.tees.mad.habitloop.domain.models.Habit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class HabitLoopRepositoryImp(
    private val habitDao: HabitDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : HabitLoopRepository {

    private val userId: String?
        get() = auth.currentUser?.uid

    override suspend fun insertHabit(habit: Habit) {
        habitDao.upsertHabit(habit = habit.toEntity())
        userId?.let {
            firestore.collection(USERS_COLLECTION).document(it)
                .collection(HABITS_COLLECTION).document(habit.id)
                .set(habit).await()
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.upsertHabit(habit = habit.toEntity())
        userId?.let {
            firestore.collection(USERS_COLLECTION).document(it)
                .collection(HABITS_COLLECTION).document(habit.id)
                .set(habit).await()
        }
    }

    override fun getHabits(): Flow<List<Habit>> = channelFlow {
        val localDataJob = launch {
            habitDao.getHabits().map { habitEntities ->
                habitEntities.map { it.toDomain() }
            }.collect { habits ->
                send(habits)
            }
        }

        try {
            syncWithFirebase()
        } catch (e: Exception) {
            // In a real app, you would log this error.
        }

        awaitClose {
            localDataJob.cancel()
        }
    }

    override suspend fun syncWithFirebase() {
        userId?.let { uid ->
            val snapshot = firestore.collection(USERS_COLLECTION).document(uid)
                .collection(HABITS_COLLECTION).get().await()

            val firebaseHabits = snapshot.toObjects(Habit::class.java)
            habitDao.insertAll(firebaseHabits.map { it.toEntity() })
        }
    }

    override suspend fun getHabitStats(): Map<String, Int> {
        val habits = habitDao.getHabits().first()
        val totalHabits = habits.size
        var currentStreak = 0
        var longestStreak = 0

        if (habits.isNotEmpty()) {
            val completedHabits = habits.count { it.isCompleted }
            val completionRate = if (totalHabits > 0) (completedHabits * 100) / totalHabits else 0

            val sortedHabits = habits.sortedByDescending { it.lastCompletedDate }
            var currentStreakCount = 0
            var longestStreakCount = 0
            var lastDate: Calendar? = null

            for (habit in sortedHabits) {
                if (habit.isCompleted) {
                    val completedCal = Calendar.getInstance().apply { timeInMillis = habit.lastCompletedDate }
                    if (lastDate == null || isConsecutiveDay(completedCal, lastDate)) {
                        currentStreakCount++
                    } else {
                        if (currentStreakCount > longestStreakCount) {
                            longestStreakCount = currentStreakCount
                        }
                        currentStreakCount = 1
                    }
                    lastDate = completedCal
                }
            }

            if (currentStreakCount > longestStreakCount) {
                longestStreakCount = currentStreakCount
            }
            currentStreak = currentStreakCount
            longestStreak = longestStreakCount

            return mapOf(
                "totalHabits" to totalHabits,
                "currentStreak" to currentStreak,
                "longestStreak" to longestStreak,
                "completionRate" to completionRate
            )
        }

        return mapOf(
            "totalHabits" to 0,
            "currentStreak" to 0,
            "longestStreak" to 0,
            "completionRate" to 0
        )
    }

    private fun isConsecutiveDay(day1: Calendar, day2: Calendar): Boolean {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) &&
                day1.get(Calendar.DAY_OF_YEAR) == day2.get(Calendar.DAY_OF_YEAR) - 1
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val HABITS_COLLECTION = "habits"
    }
}
