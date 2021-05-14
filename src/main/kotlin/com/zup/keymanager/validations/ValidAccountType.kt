package com.zup.keymanager.validations

import com.zup.keymanager.proto.PixKeyRequest
import com.zup.keymanager.proto.PixKeyRequest.*
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Constraint(validatedBy=[ValidAccountTypeValidator::class])
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, CONSTRUCTOR)
annotation class ValidAccountType(val message: String = "Must be a valid account type")

@Singleton
class ValidAccountTypeValidator : ConstraintValidator<ValidAccountType, AccountType> {
    override fun isValid(
        value: AccountType,
        annotationMetadata: AnnotationValue<ValidAccountType>,
        context: ConstraintValidatorContext
    ): Boolean = value != AccountType.UNKNOWN_ACCOUNT_TYPE && value != AccountType.UNRECOGNIZED
}