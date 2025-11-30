package com.uk.ac.tees.mad.habitloop.domain.util

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put

suspend inline fun <reified T> HttpClient.get(
    route: String,
): HttpResult<T, DataError.Remote> {
    return httpResult {
        get(route).body()
    }
}

suspend inline fun <reified T> HttpClient.post(
    route: String,
): HttpResult<T, DataError.Remote> {
    return httpResult {
        post(route).body()
    }
}

suspend inline fun <reified T> HttpClient.put(
    route: String,
): HttpResult<T, DataError.Remote> {
    return httpResult {
        put(route).body()
    }
}

suspend inline fun <reified T> HttpClient.delete(
    route: String,
): HttpResult<T, DataError.Remote> {
    return httpResult {
        delete(route).body()
    }
}
