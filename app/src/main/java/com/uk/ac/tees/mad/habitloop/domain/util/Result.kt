package com.uk.ac.tees.mad.habitloop.domain.util

sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: com.uk.ac.tees.mad.habitloop.domain.util.Error>(val error: E): Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>
