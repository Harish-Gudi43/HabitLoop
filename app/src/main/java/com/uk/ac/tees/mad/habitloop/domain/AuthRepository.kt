package com.uk.ac.tees.mad.habitloop.domain

import uk.ac.tees.mad.bookly.domain.util.DataError
import uk.ac.tees.mad.bookly.domain.util.EmptyResult

interface AuthRepository {

     suspend fun signIn(email : String, password : String) : EmptyResult<DataError.Firebase>

    suspend fun signUp(email : String, password : String,name: String) : EmptyResult<DataError.Firebase>

    suspend fun forgotPassword(email : String): EmptyResult<DataError.Firebase>

    suspend fun logOut() : EmptyResult<DataError.Firebase>

}