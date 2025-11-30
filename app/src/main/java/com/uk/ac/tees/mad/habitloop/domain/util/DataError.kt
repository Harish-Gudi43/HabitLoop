package com.uk.ac.tees.mad.habitloop.domain.util

sealed interface DataError: Error {
    enum class Firebase: DataError {
        INVALID_CREDENTIALS,
        UNKNOWN
    }
    enum class Remote: DataError {
        NO_INTERNET,
        REQUEST_TIMEOUT,
        SERIALIZATION,
        SERVER_ERROR,
        BAD_REQUEST,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        CONFLICT,
        TOO_MANY_REQUESTS,
        UNKNOWN
    }
}
