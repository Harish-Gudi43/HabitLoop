package com.uk.ac.tees.mad.habitloop.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.habitloop.data.local.HabitDao
import com.uk.ac.tees.mad.habitloop.data.local.UserDao
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.models.User
import com.uk.ac.tees.mad.habitloop.domain.models.UserDto
import com.uk.ac.tees.mad.habitloop.domain.models.toDomain
import com.uk.ac.tees.mad.habitloop.domain.models.toEntity
import com.uk.ac.tees.mad.habitloop.domain.util.DataError
import com.uk.ac.tees.mad.habitloop.domain.util.EmptyResult
import com.uk.ac.tees.mad.habitloop.domain.util.firebaseResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val habitDao: HabitDao
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            requireNotNull(user) { "firebase user was null after successful registration" }
            val userProfileData = mapOf(
                "name" to name,
                "email" to email,
                "uid" to user.uid,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(user.uid).set(userProfileData).await()
        }
    }

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            firebaseAuth.sendPasswordResetEmail(email).await()
        }
    }

    override suspend fun logOut(): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            firebaseAuth.signOut()
            userDao.clearUsers()
            habitDao.clearHabits()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            return flowOf(null)
        }

        return callbackFlow {
            // Observe local data and send it to the UI
            val localDataJob = launch {
                userDao.getUser(userId).collect { userEntity ->
                    send(userEntity?.toDomain())
                }
            }

            // Fetch remote data once
            launch {
                try {
                    val remoteUserDto = firestore.collection("users").document(userId).get().await().toObject(UserDto::class.java)
                    remoteUserDto?.let {
                        userDao.upsertUser(it.toDomain().toEntity())
                    }
                } catch (e: Exception) {
                    Log.e("AuthRepositoryImpl", "Failed to fetch remote user data for sync.", e)
                }
            }

            awaitClose { localDataJob.cancel() }
        }
    }

    override suspend fun updateUser(user: User): EmptyResult<DataError.Firebase> {
        userDao.upsertUser(user.toEntity())
        return firebaseResult {
            firestore.collection("users").document(user.uid).set(user).await()
        }
    }
}
