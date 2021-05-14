package com.zup.keymanager.validations

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.zup.keymanager.extensions.toFieldErrors
import com.zup.keymanager.extensions.with
import com.zup.keymanager.proto.ErrorResponse
import io.grpc.Status
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Validated @Singleton
class BeanValidator<T>(private val validator: Validator<T>) {

    fun validate(target: T): Result<T, ErrorResponse> {

        try {
            validator.validateConstructor(target)
        } catch (e: ConstraintViolationException) {
            return Err(Status.INVALID_ARGUMENT with e.constraintViolations.toFieldErrors())
        }

        try {
            validator.validateWithCustomStatus(target)
        } catch (e: ConstraintViolationException) {
            return Err(validator.toCustomError(e))
        }

        return Ok(target)
    }

    fun <R> with(validator: Validator<R>): BeanValidator<R> {
        return BeanValidator(validator)
    }

}

interface Validator<T> {
    fun validateConstructor(target: T)
    fun validateWithCustomStatus(target: T)
    fun toCustomError(e: ConstraintViolationException): ErrorResponse
}