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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
            userDao.clear()
            habitDao.clear()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return firebaseAuth.currentUser?.uid?.let { userId ->
            userDao.getUser(userId).flatMapLatest { userEntity ->
                flow {
                    // 1. Emit the local data first
                    emit(userEntity?.toDomain())

                    // 2. Then, try to fetch fresh data from the remote source
                    try {
                        val remoteUserDto = firestore.collection("users").document(userId).get().await().toObject(UserDto::class.java)
                        remoteUserDto?.let { dto ->
                            val freshUser = dto.toDomain()
                            // 3. Update the local cache
                            userDao.upsertUser(freshUser.toEntity())
                            // 4. Emit the fresh data to the UI
                            emit(freshUser)
                        }
                    } catch (e: Exception) {
                        Log.e("AuthRepositoryImpl", "Failed to fetch remote user data.", e)
                        // If the network fails, we just log the error. The user still sees the cached data.
                    }
                }
            }
        } ?: flow { emit(null) }
    }

    override suspend fun updateUser(user: User): EmptyResult<DataError.Firebase> {
        // 1. Update the local cache for an instant UI update
        userDao.upsertUser(user.toEntity())

        // 2. Update the remote source in the background
        return firebaseResult {
            firestore.collection("users").document(user.uid).set(user).await()
        }
    }
}
