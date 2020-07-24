package com.kekmech

import io.ktor.client.statement.*
import io.ktor.http.*

fun String?.checkIsValidGroupNumber(): String {
    if (!(this != null && matches("[а-яА-Я0-9-]{5,20}".toRegex())))
        throw InvalidArgumentException("INVALID_GROUP_NUMBER")
    return this.toUpperCase().replace("-0", "-")
}

fun HttpResponse.checkGroupFound(): HttpResponse {
    if (status != HttpStatusCode.Found)
        throw MpeiBackendUnexpectedBehaviorException("GROUP_NOT_FOUND")
    return this
}

fun HttpResponse.checkCode(statusCode: HttpStatusCode): HttpResponse {
    if (status != statusCode)
        throw MpeiBackendUnexpectedBehaviorException()
    return this
}

fun<T : Any> T.assertUnexpectedBehavior(errorMessage: String = "ERROR_PARSE_GROUP_ID", predicate: (T) -> Boolean): T {
    if (!predicate(this))
        throw MpeiBackendUnexpectedBehaviorException(errorMessage)
    return this
}