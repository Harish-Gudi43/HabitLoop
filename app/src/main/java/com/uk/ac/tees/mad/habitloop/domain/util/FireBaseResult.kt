package com.uk.ac.tees.mad.habitloop.domain.util

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

inline fun <T> firebaseResult(action: () -> T): Result<T, DataError.Firebase> {
    return try {
        Result.Success(action())
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.Error(DataError.Firebase.INVALID_CREDENTIALS)
    } catch (e: Exception) {
        Result.Error(DataError.Firebase.UNKNOWN)
    }
}
