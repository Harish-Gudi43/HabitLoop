package com.uk.ac.tees.mad.habitloop.domain

import com.uk.ac.tees.mad.habitloop.domain.util.DataError
import com.uk.ac.tees.mad.habitloop.domain.util.EmptyResult

interface AuthRepository {
    suspend fun signIn(email: String, password: String): EmptyResult<DataError.Firebase>
    suspend fun signUp(email: String, password: String, name: String): EmptyResult<DataError.Firebase>
    suspend fun forgotPassword(email: String): EmptyResult<DataError.Firebase>
    suspend fun logOut(): EmptyResult<DataError.Firebase>
}
