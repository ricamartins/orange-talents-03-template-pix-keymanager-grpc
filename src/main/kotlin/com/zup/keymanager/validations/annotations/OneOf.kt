package com.zup.keymanager.validations.annotations

import com.zup.keymanager.proto.PixKeyInfoRequest
import javax.inject.Singleton
import javax.validation.*
import kotlin.reflect.KClass

@Constraint(validatedBy = [OneOfValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class OneOf(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

@Singleton
class OneOfValidator(val validator: Validator): ConstraintValidator<OneOf, Any> {

    override fun isValid(value: Any, context: ConstraintValidatorContext): Boolean {
        validator.forExecutables()
        return when(value) {
            is PixKeyInfoRequest -> { validateInfoRequest(value)
                    .let { if (it.isNotEmpty()) throw ConstraintViolationException(it) else true } }
            else -> true //// throw Status.INTERNAL with "Annotated type not supported"
        }
    }

    private fun validateInfoRequest(request: PixKeyInfoRequest): MutableSet<ConstraintViolation<Any>> {
        return if (request.hasInfoPair()) validator.validate(request.infoPair)
        else validator.validate(request.keyValue)
    }
}