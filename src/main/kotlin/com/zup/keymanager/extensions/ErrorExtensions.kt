package com.zup.keymanager.extensions

import com.fasterxml.jackson.databind.ObjectMapper
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

fun Exception.toStatusException(): StatusRuntimeException {
    return when(this) {
        is StatusRuntimeException -> this
        is ConstraintViolationException -> Status.INVALID_ARGUMENT with this.constraintViolations.toFieldErrors()
        is HttpClientResponseException -> Status.INTERNAL with "Something went wrong"
        else -> throw this
    }
}

fun Set<ConstraintViolation<*>>.toFieldErrors(): String {
    return ObjectMapper().writeValueAsString(this.map { "${it.field()}: ${it.message}"})
}

private fun ConstraintViolation<*>.field() = this.propertyPath.last().toString()

infix fun Status.with(message: String): StatusRuntimeException {
    return this.withDescription(message).asRuntimeException()
}

fun Status.formatted() = "${this.code.value()} ${this.code}"
