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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HabitLoopRepositoryImp(
    private val habitDao: HabitDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : HabitLoopRepository {

    private val userId: String?
        get() = auth.currentUser?.uid

    override suspend fun insertHabit(habit: Habit) {
        // Insert into Room first
        habitDao.insertHabit(habit = habit.toEntity())

        // Then, insert into Firebase
        userId?.let { uid ->
            firestore.collection(USERS_COLLECTION).document(uid)
                .collection(HABITS_COLLECTION).document(habit.id)
                .set(habit).await()
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        // Update in Room first
        habitDao.insertHabit(habit = habit.toEntity()) // Using insertHabit with OnConflictStrategy.REPLACE acts as an update

        // Then, update in Firebase
        userId?.let { uid ->
            firestore.collection(USERS_COLLECTION).document(uid)
                .collection(HABITS_COLLECTION).document(habit.id)
                .set(habit).await() // `set` will overwrite the document, which works as an update
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

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val HABITS_COLLECTION = "habits"
    }
}
