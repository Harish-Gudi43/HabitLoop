package com.uk.ac.tees.mad.habitloop.domain.util

sealed interface Result<out D, out E> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Failure<out E>(val error: E) : Result<Nothing, E>
}

inline fun <D, E> Result<D, E>.onSuccess(action: (D) -> Unit): Result<D, E> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <D, E> Result<D, E>.onFailure(action: (E) -> Unit): Result<D, E> {
    if (this is Result.Failure) {
        action(error)
    }
    return this
}
