package com.zup.keymanager.validations.annotations

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@Constraint(validatedBy=[BlankValidator::class])
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, CONSTRUCTOR)
annotation class Blank(val message: String = "Must be null or blank",
                       val groups: Array<KClass<*>> = [],
                       val payload: Array<KClass<out Payload>> = []
)

@Singleton
class BlankValidator : ConstraintValidator<Blank, String> {
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<Blank>,
        context: ConstraintValidatorContext
    ): Boolean = value.isNullOrBlank()
}