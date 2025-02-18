package com.zup.keymanager.validations.annotations

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@Constraint(validatedBy=[])
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, CONSTRUCTOR)
@Pattern(
    regexp = "^\\+[1-9][0-9]\\d{1,14}\$",
    message = "Must be a valid phone number"
)
annotation class Phone(val message: String = "Must be a valid phone number",
                       val groups: Array<KClass<*>> = [],
                       val payload: Array<KClass<out Payload>> = [])
