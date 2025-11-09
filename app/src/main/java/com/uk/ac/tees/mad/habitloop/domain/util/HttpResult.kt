package uk.ac.tees.mad.bookly.domain.util

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import java.io.IOException
import kotlinx.coroutines.CancellationException

suspend fun <T> httpResult(block: suspend () -> T): Result<T, DataError.Remote> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.Failure(e.toHttpError())
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