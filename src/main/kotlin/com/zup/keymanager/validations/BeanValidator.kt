package com.zup.keymanager.validations

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.zup.keymanager.extensions.toFieldErrors
import com.zup.keymanager.extensions.with
import com.zup.keymanager.proto.ErrorResponse
import com.zup.keymanager.proto.PixKeyRequest
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.inject.InjectionPoint
import javax.validation.ConstraintViolationException
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

class BeanValidator<T>(private val validator: Validator<T>) {

    fun validate(target: T): Result<T, ErrorResponse> {

        try {
            validator.validateIllegalArguments(target)
        } catch (e: ConstraintViolationException) {
            return Err(Status.INVALID_ARGUMENT with e.constraintViolations.toFieldErrors())
        }

        if (validator is CustomStatusValidator) {

            validator.customStatusValidations(target)
                .zip(validator.customErrorMappers())
                .forEach { (validation, mapper) ->
                    try {
                        validation.invoke()
                    } catch (e: ConstraintViolationException) {
                        return Err(mapper.invoke(e))
                    }
                }
        }

        return Ok(target)
    }

}

interface Validator<T> {
    fun validateIllegalArguments(target: T)
}

interface CustomStatusValidator<T> : Validator<T> {
    fun customStatusValidations(target: T): List<() -> Unit>
    fun customErrorMappers(): List<(ConstraintViolationException) -> ErrorResponse>
}


@Factory
class BeanValidatorFactory(
    private val createRequestValidator: PixKeyCreateRequestValidator,
    private val deleteRequestValidator: PixKeyDeleteRequestValidator
) {

    @Prototype
    fun beanValidator(injectionPoint: InjectionPoint<*>): BeanValidator<*> {
        val validator: String = injectionPoint.annotationMetadata.stringValue(ValidatorType::class.java).get()
        return when(validator) {
            "PixKeyRequest" -> BeanValidator(createRequestValidator)
            "PixKeyDeleteRequest" -> BeanValidator(deleteRequestValidator)
            else -> throw RuntimeException("No validator named as '$validator'")
        }
    }

}

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
annotation class ValidatorType(val value: String = "")
