package com.uk.ac.tees.mad.habitloop.domain.util

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType


suspend inline fun <reified T, reified E> HttpClient.safeRequest(
    crossinline block: suspend (HttpClient) -> HttpResponse
): HttpResult<T, E> {
    return try {
        val response = block(this)
        HttpResult.Success(response.body())
    } catch (e: Exception) {
        HttpResult.Failure(e as E)
    }
}

suspend inline fun <reified T> HttpClient.get(
    route: String
): T = safeRequest<T, DataError.Remote> {
    it.get(route)
}.getOrNull()!!

suspend inline fun <reified T> HttpClient.post(
    route: String,
    body: Any? = null
): T = safeRequest<T, DataError.Remote> {
    it.post(route) {
        contentType(ContentType.Application.Json)
        body?.let { setBody(it) }
    }
}.getOrNull()!!

suspend inline fun <reified T> HttpClient.put(
    route: String,
    body: Any? = null
): T = safeRequest<T, DataError.Remote> {
    it.put(route) {
        contentType(ContentType.Application.Json)
        body?.let { setBody(it) }
    }
}.getOrNull()!!

suspend inline fun <reified T> HttpClient.patch(
    route: String,
    body: Any? = null
): T = safeRequest<T, DataError.Remote> {
    it.patch(route) {
        contentType(ContentType.Application.Json)
        body?.let { setBody(it) }
    }
}.getOrNull()!!

suspend inline fun <reified T> HttpClient.delete(
    route: String,
    body: Any? = null
): T = safeRequest<T, DataError.Remote> {
    it.delete(route) {
        contentType(ContentType.Application.Json)
        body?.let { setBody(it) }
    }
}.getOrNull()!!

fun <T, E> HttpResult<T, E>.getOrNull(): T? {
    return when (this) {
        is HttpResult.Success -> data
        is HttpResult.Failure -> null
    }
}

