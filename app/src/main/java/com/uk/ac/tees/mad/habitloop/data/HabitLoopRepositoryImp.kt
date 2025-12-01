package com.uk.ac.tees.mad.habitloop.data

import android.content.Context
import android.widget.Toast
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
    private val auth: FirebaseAuth,
    private val context: Context
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
        if (habits.isEmpty()) {
            return mapOf(
                "totalHabits" to 0,
                "currentStreak" to 0,
                "longestStreak" to 0,
                "completionRate" to 0
            )
        }

        val totalHabits = habits.size
        val completedOnceCount = habits.count { it.lastCompletedDate > 0 }
        val completionRate = if (totalHabits > 0) (completedOnceCount * 100) / totalHabits else 0

        val completionDays = habits
            .filter { it.lastCompletedDate > 0 }
            .map { habit ->
                Calendar.getInstance().apply { timeInMillis = habit.lastCompletedDate }
            }
            .map { cal -> cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR) } // Create a unique day identifier
            .toSet()
            .sorted()

        var longestStreak = 0
        var currentStreak = 0

        if (completionDays.isNotEmpty()) {
            var currentStreakLength = 0
            if (completionDays.isNotEmpty()) {
                longestStreak = 1
                currentStreakLength = 1
                for (i in 1 until completionDays.size) {
                    if (completionDays[i] == completionDays[i - 1] + 1) {
                        currentStreakLength++
                    } else {
                        currentStreakLength = 1
                    }
                    if (currentStreakLength > longestStreak) {
                        longestStreak = currentStreakLength
                    }
                }
            }

            val today = Calendar.getInstance()
            val todayIdentifier = today.get(Calendar.YEAR) * 1000 + today.get(Calendar.DAY_OF_YEAR)
            var dayToCheck = todayIdentifier
            var streak = 0
            while (completionDays.contains(dayToCheck)) {
                streak++
                dayToCheck--
            }
            currentStreak = streak
        }

        return mapOf(
            "totalHabits" to totalHabits,
            "currentStreak" to currentStreak,
            "longestStreak" to longestStreak,
            "completionRate" to completionRate
        )
    }

    override suspend fun backupHabits() {
        userId?.let {
            val habits = habitDao.getHabits().first()
            for (habit in habits) {
                firestore.collection(USERS_COLLECTION).document(it)
                    .collection(HABITS_COLLECTION).document(habit.id)
                    .set(habit).await()
            }
            Toast.makeText(context, "Backup successful", Toast.LENGTH_SHORT).show()
        }
    }

    override suspend fun restoreHabits() {
        syncWithFirebase()
        Toast.makeText(context, "Restore successful", Toast.LENGTH_SHORT).show()
    }

    override suspend fun clearHabits() {
        habitDao.clearHabits()
        userId?.let { uid ->
            val habitsCollection = firestore.collection(USERS_COLLECTION).document(uid)
                .collection(HABITS_COLLECTION)
            val snapshot = habitsCollection.get().await()
            for (document in snapshot.documents) {
                habitsCollection.document(document.id).delete().await()
            }
        }
        Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val HABITS_COLLECTION = "habits"
    }
}
