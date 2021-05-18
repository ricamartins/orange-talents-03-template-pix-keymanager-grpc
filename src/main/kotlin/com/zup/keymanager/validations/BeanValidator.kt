package com.zup.keymanager.validations

import com.zup.keymanager.extensions.toFieldErrors
import com.zup.keymanager.extensions.with
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.inject.InjectionPoint
import javax.validation.ConstraintViolationException
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

class BeanValidator<T>(private val validator: Validator<T>) {

    fun validate(target: T): T {

        try {
            validator.validateIllegalArguments(target)
        } catch (e: ConstraintViolationException) {
            throw Status.INVALID_ARGUMENT with e.constraintViolations.toFieldErrors()
        }

        if (validator is CustomStatusValidator)
            validator.customStatusValidations(target).forEach { it.invoke() }

        return target
    }

}

interface Validator<T> {
    fun validateIllegalArguments(target: T)
}

interface CustomStatusValidator<T> : Validator<T> {
    fun customStatusValidations(target: T): List<() -> Unit>
}
