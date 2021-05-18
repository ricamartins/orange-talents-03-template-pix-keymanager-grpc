package com.zup.keymanager.validations

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.inject.InjectionPoint

@Factory
class BeanValidatorFactory(
    private val createRequestValidator: PixKeyCreateRequestValidator,
    private val deleteRequestValidator: PixKeyDeleteRequestValidator
) {

    @Prototype
    fun beanValidator(injectionPoint: InjectionPoint<*>): BeanValidator<*> {
        val validator: String = injectionPoint.annotationMetadata.stringValue(ValidatorType::class.java).get()
        return when(validator) {
            "PixKeyCreateRequest" -> BeanValidator(createRequestValidator)
            "PixKeyDeleteRequest" -> BeanValidator(deleteRequestValidator)
            else -> throw RuntimeException("No validator named as '$validator'")
        }
    }

}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ValidatorType(val value: String = "")