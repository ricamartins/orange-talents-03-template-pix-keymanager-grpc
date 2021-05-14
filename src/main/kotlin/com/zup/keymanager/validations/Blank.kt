package com.zup.keymanager.validations

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Constraint(validatedBy=[BlankValidator::class])
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, CONSTRUCTOR)
annotation class Blank(val message: String = "Must be null or blank")

@Singleton
class BlankValidator : ConstraintValidator<Blank, String> {
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<Blank>,
        context: ConstraintValidatorContext
    ): Boolean = value.isNullOrBlank()
}