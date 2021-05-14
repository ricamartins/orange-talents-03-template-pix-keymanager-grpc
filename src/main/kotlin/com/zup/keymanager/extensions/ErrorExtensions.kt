package com.zup.keymanager.extensions

import com.zup.keymanager.proto.ErrorResponse
import com.zup.keymanager.proto.FieldError
import io.grpc.Status
import javax.validation.ConstraintViolation

fun Set<ConstraintViolation<*>>.toFieldErrors(): List<FieldError> {
    return this.map { it.field() to it.message}.map { it.toFieldError() }
}

private fun Pair<String, String>.toFieldError(): FieldError {
    return FieldError.newBuilder().setField(this.first).setMessage(this.second).build()
}

private fun String.toErrorMessage(): FieldError {
    return FieldError.newBuilder().setMessage(this).build()
}

fun ConstraintViolation<*>.field() = this.propertyPath.iterator().asSequence().last().toString()

infix fun Status.with(fieldErrors: List<FieldError>): ErrorResponse {
    return ErrorResponse.newBuilder()
        .setStatus(this.formatted()).addAllErrors(fieldErrors).build()
}

infix fun Status.with(fieldErrorPair: Pair<String, String>): ErrorResponse {
    return ErrorResponse.newBuilder()
        .setStatus(this.formatted()).addErrors(fieldErrorPair.toFieldError()).build()
}

infix fun Status.with(message: String): ErrorResponse {
    return ErrorResponse.newBuilder()
        .setStatus(this.formatted()).addErrors(message.toErrorMessage()).build()
}

fun Status.formatted() = "${this.code.value()} ${this.code}"