package com.uk.ac.tees.mad.habitloop.domain.util

import kotlinx.coroutines.CancellationException

typealias EmptyResult<E> = Result<Unit, E>

inline fun <E> firebaseResult(block: () -> Unit): EmptyResult<E> {
    return try {
        block()
        Result.Success(Unit)
    } catch (e: Exception) {
        if (e is CancellationException) {
            throw e
        }
        Result.Failure(e as E)
    }
}
