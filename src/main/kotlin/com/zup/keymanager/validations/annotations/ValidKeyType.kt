package com.zup.keymanager.validations.annotations

import com.zup.keymanager.proto.KeyType
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Constraint(validatedBy=[ValidKeyTypeValidator::class])
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, CONSTRUCTOR)
annotation class ValidKeyType(val message: String = "Must be a valid key type")

@Singleton
class ValidKeyTypeValidator : ConstraintValidator<ValidKeyType, KeyType> {
    override fun isValid(
        value: KeyType,
        annotationMetadata: AnnotationValue<ValidKeyType>,
        context: ConstraintValidatorContext
    ) = value != KeyType.UNKNOWN_KEY_TYPE && value != KeyType.UNRECOGNIZED
}