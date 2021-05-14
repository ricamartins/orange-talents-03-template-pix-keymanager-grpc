package com.zup.keymanager.validations

import com.zup.keymanager.pixkey.PixKeyRepository
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint

@Constraint(validatedBy=[UniqueValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR)
annotation class Unique

@Singleton
class UniqueValidator(private val repository: PixKeyRepository) : ConstraintValidator<Unique, String> {

    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<Unique>,
        context: ConstraintValidatorContext
    ): Boolean =  value?.let { !repository.existsByKeyValue(it) } ?: true
}
