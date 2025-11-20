package com.uk.ac.tees.mad.habitloop.domain.util

sealed interface DataError {
    enum class Firebase : DataError {
        UNKNOWN_ERROR,
        USER_NOT_FOUND,
        INVALID_CREDENTIALS
    }

    enum class Remote : DataError {
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
