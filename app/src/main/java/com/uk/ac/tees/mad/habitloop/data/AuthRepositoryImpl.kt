package com.uk.ac.tees.mad.habitloop.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.habitloop.data.local.UserDao
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.models.User
import com.uk.ac.tees.mad.habitloop.domain.models.toDomain
import com.uk.ac.tees.mad.habitloop.domain.models.toEntity
import com.uk.ac.tees.mad.habitloop.domain.util.DataError
import com.uk.ac.tees.mad.habitloop.domain.util.EmptyResult
import com.uk.ac.tees.mad.habitloop.domain.util.Result
import com.uk.ac.tees.mad.habitloop.domain.util.firebaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
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
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return firebaseAuth.currentUser?.uid?.let {
            userDao.getUser(it).flatMapLatest { userEntity ->
                flow {
                    emit(userEntity?.toDomain())
                    val remoteUser = firestore.collection("users").document(it).get().await().toObject(User::class.java)
                    remoteUser?.let { user ->
                        userDao.upsertUser(user.toEntity())
                        emit(user)
                    }
                }
            }
        } ?: flow { emit(null) }
    }

    override suspend fun updateUser(user: User): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            firestore.collection("users").document(user.uid).set(user).await()
        }
    }
}
