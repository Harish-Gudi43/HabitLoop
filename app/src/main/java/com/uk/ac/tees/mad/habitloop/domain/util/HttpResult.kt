package com.uk.ac.tees.mad.habitloop.domain.util

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import java.io.IOException
import kotlinx.coroutines.CancellationException

sealed interface HttpResult<out D, out E: Error> {
    data class Success<out D>(val data: D) : HttpResult<D, Nothing>
    data class Failure<out E: Error>(val error: E) : HttpResult<Nothing, E>
}

suspend fun <T> httpResult(block: suspend () -> T): HttpResult<T, DataError.Remote> {
    return try {
        HttpResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        HttpResult.Failure(e.toHttpError())
    }
}

private fun Throwable.toHttpError(): DataError.Remote = when (this) {
    is IOException -> DataError.Remote.NO_INTERNET
    is HttpRequestTimeoutException -> DataError.Remote.REQUEST_TIMEOUT
    is JsonConvertException -> DataError.Remote.SERIALIZATION
    is ServerResponseException -> DataError.Remote.SERVER_ERROR
    is ClientRequestException -> when (response.status.value) {
        400 -> DataError.Remote.BAD_REQUEST
        401 -> DataError.Remote.UNAUTHORIZED
        403 -> DataError.Remote.FORBIDDEN
        404 -> DataError.Remote.NOT_FOUND
        409 -> DataError.Remote.CONFLICT
        429 -> DataError.Remote.TOO_MANY_REQUESTS
        else -> DataError.Remote.UNKNOWN
    }
    else -> DataError.Remote.UNKNOWN
}
