package com.zup.keymanager.validations.annotations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.pixkey.PixKeyRepository
import io.grpc.Status
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [UniqueKeyValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
annotation class UniqueKeyValue(
    val message: String = "Key value is already registered",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

@Singleton
class UniqueKeyValidator(val repository: PixKeyRepository): ConstraintValidator<UniqueKeyValue, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext?): Boolean {
        return if (repository.existsByKeyValue(value))
            throw Status.ALREADY_EXISTS with "Key value is already registered"
        else true
    }
}

