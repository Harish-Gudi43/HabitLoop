package uk.ac.tees.mad.bookly.domain.util

sealed interface Error

sealed interface DataError : Error {

    enum class Remote : DataError {
        BAD_REQUEST,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        INTERNAL_SERVER_ERROR,
        SERVICE_UNAVAILABLE,
        CONFLICT,
        TOO_MANY_REQUESTS,
        SERVER_ERROR,
        PAYLOAD_TOO_LARGE,
        NO_INTERNET,
        SERIALIZATION,
        UNKNOWN,
    }

    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN
    }

    // 🔹 For Firebase/Firestore
    enum class Firebase : DataError {
        UNAUTHENTICATED,
        PERMISSION_DENIED,
        NOT_FOUND,
        ALREADY_EXISTS,
        CANCELLED,
        DEADLINE_EXCEEDED,
        FAILED_PRECONDITION,
        ABORTED,
        OUT_OF_RANGE,
        RESOURCE_EXHAUSTED,
        UNIMPLEMENTED,
        INTERNAL,
        UNAVAILABLE,
        INVALID_ARGUMENT,

        NETWORK,
        SERIALIZATION,
        UNKNOWN
    }
}